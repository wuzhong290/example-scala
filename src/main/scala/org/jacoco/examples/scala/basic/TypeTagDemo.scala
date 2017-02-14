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
    val ev21 = m2(f1)(b1)
    println(ev21.tpe)
    val f2 = new Foo
    val b2 = new f2.Bar
    val ev22 = m2(f2)(b2)
    println(ev22.tpe)
    println(ev21==ev22)
  }
}
