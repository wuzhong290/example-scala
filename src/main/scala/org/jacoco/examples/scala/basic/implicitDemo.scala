package org.jacoco.examples.scala.basic

import scala.language.implicitConversions

/**
  * Created by wuzhong on 2017/2/10.
  */

//隐式转换
class Implicit(a: A) {
  def Test(): Unit = {
    println("Implicit")
  }
}

class A {

}

object implicitDemo {
  //隐式参数
  implicit val name = "hark"
  def ImplicitMethod(implicit name: String): Unit = {
    println(name)
  }

  //隐式转换
  implicit def a2Implicit(a: A): Implicit = new Implicit(a)

  //隐式类:Int类隐式转换为JiSuan
  implicit class JiSuan(x: Int) {
    def add(a: Int): Int = a + 1
  }

  def main(args: Array[String]) {
    //隐式参数
    ImplicitMethod
    ImplicitMethod("kxy")

    //隐式转换
    val a = new A
    a.Test()

    //隐式类
    println(2.add(2))
  }
}
