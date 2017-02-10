package org.jacoco.examples.scala.akka

import akka.actor.{Props, ActorSystem,Actor}

/**
  * Created by wuzhong on 2017/2/10.
  */
object HelloRemote extends App {
  val system = ActorSystem("HelloRemoteSystem")
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! Message("The RemoteActor is alive")
}

class RemoteActor extends Actor {
  def receive = {
    case Message(msg) =>
      println(s"RemoteActor received message '$msg'")
      sender ! Message("Hello from the RemoteActor")    // 回复消息
    case _ =>
      println("RemoteActor got something unexpected.")
  }
}

case class Message(msg: String)
