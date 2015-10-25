package concurrency

import akka.routing.{ActorRefRoutee, Router, RoundRobinRoutingLogic}
import akka.event.Logging
import akka.actor.{ActorSystem, Actor, Props, Terminated}

class RoutingHelper(actorProps: Props, numOfActors: Int) extends Actor {
  var router = {
    val routees = Vector.fill(numOfActors) {
      val r = context.actorOf(actorProps)
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case w: String =>
      router.route(w, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(actorProps)
      context watch r
      router = router.addRoutee(r)
  }
}

object RoutingHelper {
  def props(actorProps: Props, numOfActors: Int) =
    Props(new RoutingHelper(actorProps, numOfActors))
}
