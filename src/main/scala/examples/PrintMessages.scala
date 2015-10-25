package examples

import kafka.consumer.HighLevelConsumer

object PrintMessages /*extends App*/ {
  def printString(msg: Array[Byte], threadId: Int): Boolean = {
    try {
      println(new String(msg))
    } catch {
      case e: Throwable => return false
    }
    return true
  }

  val consumer = new HighLevelConsumer()
  consumer.start(printString)
}
