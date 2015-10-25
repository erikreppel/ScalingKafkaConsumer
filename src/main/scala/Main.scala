package ScalingConsumer

import kafka.consumer.HighLevelConsumer
import kafka.producer.KafkaProducer
import kafka.utils.Logging
import kafka.utils.{ZkUtils, ZKStringSerializer, Logging}
import org.I0Itec.zkclient.ZkClient

import java.util.Properties

import scala.collection.mutable.Map
import scala.collection.Seq
import scala.io.Source

import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.event.Logging
import concurrency.RoutingHelper


object Main extends App with Logging {

  /****************************************************************************/
  // General helper values, uncomment to use, they are helpful.
  /****************************************************************************/
  // val properties = new Properties
  // val propFileUrl = getClass.getResource("/consumer.properties")
  // if (propFileUrl != null) {
  //   val source = Source.fromURL(propFileUrl)
  //   properties.load(source.bufferedReader())
  // }

  // val topic = properties.getProperty("topic")
  // val zookeeper = properties.getProperty("zookeeper.connect")
  // val zkClient = new ZkClient(zookeeper, 30000, 30000, ZKStringSerializer)
  // val partitions = ZkUtils.getPartitionsForTopics(zkClient, Seq(topic))
  // val numPartitions = partitions(topic).size

  /****************************************************************************/
  // Your code goes below
  /****************************************************************************/
  info("Your code goes in this file!\nAlternatively, try one of the examples.")

  //val consumer = new HighLevelConsumer()
  //consumer.start(nameOfFunction)

}
