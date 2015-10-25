package utilities

import java.io.{FileInputStream, FileWriter}
import utilities.DvstHelpers.hex2bytes
import akka.actor.Actor

object RedundancyHelpers {
  def writeToFile(message: String) = {
    val f = new FileWriter("DeliveryFailures.txt", true)
    f.write(message + '\n')
    f.close()
  }

  def actorWriteToFile: Actor.Receive = {
    case msg: String =>
      // the output file contains failed messages in the form
      // of an array of bytes stored as a string
      val msgBytes = hex2bytes(msg)
      writeToFile(new String(msgBytes))
    case msg: Array[Byte] =>
      writeToFile(new String(msg))
  }
}
