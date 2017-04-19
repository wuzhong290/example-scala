package org.jacoco.examples.scala.basic
import scala.reflect.runtime.universe._
/**
  * Created by dell on 2017/2/14.
  */
class Foo{
  class Bar
}
class Pair[T : Ordering](val first: T, val second: T) {
  // 1. 编译后，Pair里面会有一个隐式的Ordering[T]的字段
  // 2. 定义一个带有隐式参数的方法，调用方法时，会自动获取Pair类的隐式字段Ordering[T]
  def smaller(implicit ord: Ordering[T]) =
    if (ord.compare(first, second) < 0 ) first else second
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
    val p = new Pair(100,20)
    println(p.smaller)
  }
}
