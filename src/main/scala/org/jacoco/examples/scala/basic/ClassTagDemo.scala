package org.jacoco.examples.scala.basic
import scala.reflect.ClassTag
/**
  * Created by dell on 2017/2/14.
  */
object ClassTagDemo {
  //:_*作为一个整体，告诉编译器你希望将某个参数当作参数序列处理！
  // 例如Array[T](elems: _*)就是将elems当作参数序列处理。
  def mkArray[T:ClassTag](elems: T*) = Array[T](elems: _*)

  def main(args: Array[String]) {
    //使用泛型的时候加上的类型参数，会在编译的时候去掉，这个过程就称为类型擦除
    //ClassTag会帮我们存储T的信息，
    //如下面，传入1,2根据类型推到可以指定T是Int类型，
    //这时候ClassTag就可以把Int类型这个信息传递给编译器。
    //ClassTag运行时指定在编译的时候无法指定的类型信息。
    val t = ClassTagDemo.mkArray(1, 2)
    println(t.clone().apply(0)+":"+ t.clone().apply(1))
  }
}
