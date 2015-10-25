package examples

import kafka.producer.KafkaProducer
import kafka.consumer.ActorConsumer
import akka.actor.Actor
import kafka.utils.Logging

object ActorConsumerTopicToTopic extends Logging /*with App*/ {

  // kafkaLoc is a comma seperated list of kafka servers
  // Ex: localhost:9092,notlocalhost:9092,server:9092
  val kafkaLoc = "localhost:9092"
  // name of the topic to put messages into
  val outTopic = "testing.topicToTopic"

  val producer = new KafkaProducer(outTopic, kafkaLoc, synchronously=false)

  def topicToTopic: Actor.Receive = {
    case message: String =>
      //println(s"Sending to $outTopic")
      producer.send(message)
    case _ => info("Something happened and I dont know, it wasn't a string")
  }

  val actorConsumer = new ActorConsumer()
  actorConsumer.start(topicToTopic)

}
