package org.jacoco.examples.scala

object HelloWorld {
  
	def message(bigger : Boolean) = {
		if (bigger) {
			"Hello Universe!"
		} else {
			"Hello World!"
		}
	}

}
