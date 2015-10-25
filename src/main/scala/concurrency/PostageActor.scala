package concurrency

import akka.actor.{ Actor, ActorRef, Props, ActorSystem }
import akka.event.Logging
import scalaj.http.{HttpResponse, Http}

object PostageActor {
    def props(url: String) = Props(new PostageActor(url))
}

class PostageActor(url: String) extends Actor {
  val log = Logging(context.system, this)
  var count = 0
  var startTime = System.currentTimeMillis
  def receive = {
    case msg:Int =>
      count += msg
      var timeNow = System.currentTimeMillis
      var elapsed = (timeNow - startTime) / 1000.0
      if (elapsed > 1) {
        val response: HttpResponse[String] = Http(url)
          .postForm(Seq("total" -> count.toString))
          .asString
          count = 0
          startTime = timeNow
        }
        case _ =>
        log.info("that wasnt an int")
      }
    }
