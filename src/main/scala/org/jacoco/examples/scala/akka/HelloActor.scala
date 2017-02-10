package org.jacoco.examples.scala.akka

import akka.actor.{Props, ActorSystem, Actor}
/**
  * Created by wuzhong on 2017/2/10.
  */
class HelloActor extends Actor {
  def receive = {
    case "hello" => println("world")
    case _ => println("huh?")
  }
}

object HelloActor{
  def main(args: Array[String]) {
    val system = ActorSystem("System")
    val helloActor = system.actorOf(Props(new HelloActor()))
    helloActor ! "hello"
    helloActor ! "buenos dias"
    helloActor ! "buenos dias1"
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  helloActor ! "hello"
  helloActor ! "buenos dias"
  helloActor ! "buenos dias1"
}