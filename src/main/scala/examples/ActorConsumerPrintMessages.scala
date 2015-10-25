package example

import kafka.utils.Logging
import kafka.consumer.ActorConsumer
import akka.actor.Actor
import utilities.RedundancyHelpers.writeToFile

object ActorConsumerPrintMessages extends Logging /*with App*/ {
  var counter = 0
  def receivePrint(): Actor.Receive = {
    case msg: String =>
      try {
        println(msg)
      }
      catch {
        case e:Throwable =>
          writeToFile(msg)
          error(e)
      }

    case _ => println("That wasnt as string")
    }

  val actorConsumer = new ActorConsumer()
  actorConsumer.start(receivePrint)
}
