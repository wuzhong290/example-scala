package org.jacoco.examples.scala.basic

/**
  * Created by wuzhong on 2017/2/10.
  */
object demo1 {
    private var name = "wuzhong"
    //函数 getter方法
    val name_ = () => name
    //函数 setter方法
    val _name = (name:String) =>{
        println(this.name+":"+name)
        this.name = name
    }
    //方法 getter方法
    def getName = name
    //方法 setter方法
    def setName(name:String) = {
        println(this.name+":"+name)
        this.name = name
    }
    def main(args: Array[String]) {
      println(getName)
      println(name_())
      _name("liuyanjun")
      println(getName)
      println(name_())
    }
}
