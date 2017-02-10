package org.jacoco.examples.scala

object HelloWorld {
  
	def message(bigger : Boolean) = {
		if (bigger) {
			println("Hello Universe!")
			"Hello Universe!"
		} else {
			println("Hello World!")
			"Hello World!"
		}
	}
	message(false)
  def main(args: Array[String]) {
    message(false)
  }
}
