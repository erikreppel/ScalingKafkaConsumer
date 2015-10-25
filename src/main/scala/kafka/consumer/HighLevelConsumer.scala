package kafka.consumer

import kafka.consumer.{Consumer, KafkaStream, Whitelist, ConsumerConfig, ConsumerIterator}
import kafka.serializer.DefaultDecoder
import kafka.utils.{ZkUtils, ZKStringSerializer, Logging}
import org.I0Itec.zkclient.ZkClient

import scala.collection.{mutable, immutable, Seq}
import scala.io.Source
import java.io.{FileInputStream, FileWriter}
import java.util.Properties
import java.util.concurrent.{Executors, ExecutorService}

import akka.actor.{ Actor, ActorRef, Props, ActorSystem }
import concurrency.PostageActor


class FailedToProcessMessageException(message: String) extends Exception(message)

class HighLevelConsumer() extends Logging {

  val properties = new Properties
  val propFileUrl = getClass.getResource("/consumer.properties")
  if (propFileUrl != null) {
    val source = Source.fromURL(propFileUrl)
    properties.load(source.bufferedReader())
  }

  /*
  * Topic is not a property of ConsumerConfig
  * but in an attempt to keep all consumer settings in one place
  * I kept it in consumer.properties, thus we need to capture it
  * then remove it from the properties object we pass ConsumerConfig
  */
  val topic = properties.getProperty("topic")
  properties.remove("topic")

  // Create a consumer with our configuration
  val consumerConfig =  new ConsumerConfig(properties)
  val consumer = Consumer.create(consumerConfig)

  /*
  * We can only have one thread per partition. We can have multiple partitions
  * per thread, but it's inefficient, so we want the same number of threads
  * as partitions. To do this we ask zookeeper for the number of partitions
  * our topic has.
  */
  val zookeeper = consumerConfig.props.getString("zookeeper.connect")
  val zkClient = new ZkClient(zookeeper, 30000, 30000, ZKStringSerializer)
  val partitions = ZkUtils.getPartitionsForTopics(zkClient, Seq(topic))
  val nThreads = partitions(topic).size

  info(s"Found $nThreads partitions for $topic; starting $nThreads threads")

  val topicWhitelist = Whitelist(topic)

  val webLogProperties = new Properties
  val webLogPropUrl = getClass.getResource("/weblogging.properties")

  if (propFileUrl != null) {
    val wlSource = Source.fromURL(webLogPropUrl)
    webLogProperties.load(wlSource.bufferedReader())
  }


  val webLogging = if(webLogProperties.getProperty("logging.on") == "true") true else false

  val wlUrl = webLogProperties.getProperty("server.location")
  val actorSystem = ActorSystem("SendingSystem")
  lazy val postageActor: ActorRef = actorSystem.actorOf(PostageActor.props(wlUrl))


  /*
  * Specify a helper class to allow us to run as passed function in a thread.
  * We pass a stream (each stream is already mapped to a partition of a topic),
  * a function to apply to each message, and the ID of the thread (the thread
  * ID is mostly so we can see what each thread is doing while we debug, but
  * maybe someone will think of something clever to do with it)
  */
  class StreamOperator(f: (Array[Byte], Int) => Boolean,
                       stream: KafkaStream[Array[Byte], Array[Byte]],
                       threadId: Int) extends Runnable {
    def run() {
      val it = stream.iterator()
      while(it.hasNext()){
        var msg = it.next().message()
        var success = f(msg, threadId)
        if (webLogging) postageActor ! 1
        if (success == false) {
          val strMessage = new String(msg)
          val f = new FileWriter("DeliveryFailures.txt", true)
          try {
            f.write(strMessage + '\n')
          }
          finally {
            f.close()
            throw new FailedToProcessMessageException("Failed to process $strMessage")
          }
        }
      }
    }
  }
  val executor = Executors.newFixedThreadPool(nThreads)

  def start(runnerFunction: (Array[Byte], Int) => Boolean) {
    // streams is a Sequence
    val streams = consumer.createMessageStreamsByFilter(topicWhitelist,
                                                        nThreads,
                                                        new DefaultDecoder(),
                                                        new DefaultDecoder())
    info(s"Starting $streams.length streams")
    var threadCounter = 0
    for (stream <- streams){
      var op = new StreamOperator(runnerFunction, stream, threadCounter)
      executor.submit(op, threadCounter)
      threadCounter += 1
    }
  }

  def shutdown() = {
    info("Shutting down gracefully")
    executor.shutdown()
  }
}
