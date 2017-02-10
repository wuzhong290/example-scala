package org.jacoco.examples.scala.akka
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
/**
  * Created by wuzhong on 2017/2/10.
  */

object HelloLocal extends App {
  System.setProperty("akka.remote.netty.tcp.port", "5152")
  implicit val system = ActorSystem("LocalSystem")
  val localActor = system.actorOf(Props[LocalActor], name = "LocalActor") // the local actor
  localActor ! Start
}

class LocalActor extends Actor {

  // create the remote actor
  val remote = context.actorSelection("akka.tcp://HelloRemoteSystem@127.0.0.1:5150/user/RemoteActor")
  var counter = 0

  def receive = {
    case Start =>
      remote ! Message("Hello from the LocalActor")
    case Message(msg) =>
      println(s"LocalActor received message: '$msg'")
      if (counter < 5) {
        sender ! Message("Hello back to you")
        counter += 1
      }
    case _ =>
      println("LocalActor got something unexpected.")
  }
}

case object Start