package org.jacoco.examples.scala.basic
import scala.reflect.runtime.universe._
/**
  * Created by dell on 2017/2/14.
  */
class Foo{
  class Bar
}

object TypeTagDemo {
  def m2(f: Foo)(b: f.Bar)(implicit ev: TypeTag[f.Bar]) = ev

  def main(args: Array[String]) {
    val f1 = new Foo
    val b1 = new f1.Bar
    val ev3 = m2(f1)(b1)
    println(ev3.tpe)
  }
}
