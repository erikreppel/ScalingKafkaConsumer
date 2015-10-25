package examples

import kafka.consumer.HighLevelConsumer
import kafka.utils.Logging
import kafka.utils.{ ZkUtils, ZKStringSerializer, Logging }
import org.I0Itec.zkclient.ZkClient

import java.util.Properties

import scala.collection.mutable.Map
import scala.collection.Seq
import scala.io.Source

object CountMessages extends Logging /*with App*/ {
  /*
  * Get number of partitions so we can create a map to count
  * the number of messages each thread sees
  */
  val properties = new Properties
  val propFileUrl = getClass.getResource("/consumer.properties")
  if (propFileUrl != null) {
    val source = Source.fromURL(propFileUrl)
    properties.load(source.bufferedReader())
  }

  val topic = properties.getProperty("topic")
  val zookeeper = properties.getProperty("zookeeper.connect")
  val zkClient = new ZkClient(zookeeper, 30000, 30000, ZKStringSerializer)
  val partitions = ZkUtils.getPartitionsForTopics(zkClient, Seq(topic))
  val numPartitions = partitions(topic).size

  var count: Map[Int, Int] = Map()
  for (a <- 0 until numPartitions) {
    count += (a -> 0)
  }

  val startTime = System.currentTimeMillis

  // counts the messages that have been seen and prints results
  def countMessages(msg: Array[Byte], threadId: Int): Boolean = {
    try {
      if (count(threadId) % 100000 == 0 && count(threadId) != 0) {
        var elapsed = (System.currentTimeMillis - startTime) / 1000.0
        var mps = count(threadId) / elapsed
        var localCount = count(threadId)
        info(s"Processed $localCount messages in $elapsed ($mps msg/s) in thread $threadId")
      }
      count(threadId) += 1
    } catch {
      case e: Throwable => return false
    }
    return true
  }

  val consumer = new HighLevelConsumer()
  consumer.start(countMessages)
}
