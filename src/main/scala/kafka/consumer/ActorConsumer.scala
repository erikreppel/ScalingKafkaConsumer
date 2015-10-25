package kafka.consumer

import kafka.producer.KafkaProducer
import kafka.consumer.HighLevelConsumer
import kafka.utils.Logging
import kafka.utils.{ ZkUtils, ZKStringSerializer, Logging }
import org.I0Itec.zkclient.ZkClient

import java.util.Properties
import java.io.{FileInputStream, FileWriter}

import scala.collection.mutable.Map
import scala.collection.Seq
import scala.io.Source

import akka.actor.{ Actor, ActorRef, Props, ActorSystem }
import akka.event.Logging
import concurrency.RoutingHelper

import utilities.RedundancyHelpers.actorWriteToFile
import scalaj.http.{HttpResponse, Http}

class ActorConsumer extends Logging {
  /*
  * Get number of partitions for the topic we want to read from
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

  /*
   * Create a map so that each thread has its own producer.
   * A Map is just a key value store. The thread ID's are just the integer
   * from 0 to the number of partitions so we can have each thread reference
   * only its own producer simple by having it check its thread ID as a key in
   * the map.
   */
  class GeneralActor(receiveFunction: Actor.Receive) extends Actor {
    def receive = {
      try {
        receiveFunction
      }
      catch {
        case e: Throwable =>
          actorWriteToFile
      }
    }
  }

  /*
   * Having an object with a properties function is just handy, makes it
   * easier to pass parameters to an actor
   */
  object GeneralActor {
    def props(receiveFunction: Actor.Receive) =
      Props(new GeneralActor(receiveFunction))
  }

  val generalSystem = ActorSystem("AGeneralSystem")
  val consumer = new HighLevelConsumer()

  /* RoutingHelper takes the properties of the actor it should execute,
   * and the number of instances of that actor to create and returns
   * and ActorRef (RoutingHelper its self is an actor) which we add to
   * our actor system
   */
  def start(receiveFunction: Actor.Receive) = {
    val router = {
      generalSystem.actorOf(
        RoutingHelper.props(
          GeneralActor.props(receiveFunction),
          numOfActors = 50),
        name = "router")
    }

    // takes the thread ID and returns the actor router for that thread
    val routingMap: Map[Int, ActorRef] = Map()
    for (n <- 0 until numPartitions) {
      routingMap += (n -> router)
    }

    // A function that handles to pass from the threaded consumer to the actors
    def crossingGuard(msg: Array[Byte], threadId: Int): Boolean = {
      def bytesToHex(bytes : Array[Byte]) =
        bytes.map{ b => String.format("%02X", java.lang.Byte.valueOf(b)) }.mkString("")

      try {
        routingMap(threadId) ! bytesToHex(msg)
      }
      catch {
        case e: Throwable => return false
      }
      return true
    }

    consumer.start(crossingGuard)
  }


}
