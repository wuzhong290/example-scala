package org.jacoco.examples.scala.kafka

import kafka.javaapi.producer.Producer
import kafka.producer.{ProducerConfig, KeyedMessage}
;

/**
  * Created by wuzhong on 2017/4/19.
  */
class SparkKafkaProducer[K, V](createProducer: () => Producer[K, V]) extends Serializable {
  //This is the key idea that allows us to work around running into NotSerializableExceptions.
  //使用lazy关键字修饰变量后，只有在使用该变量时，才会调用其实例化方法。
  lazy val producer = createProducer()

  def send(message: KeyedMessage[K, V]) {
    producer.send(message)
  }

  def send(messages: java.util.List[KeyedMessage[K, V]]) {
    producer.send(messages)
  }
}

object SparkKafkaProducer {

  def apply[K, V](config: ProducerConfig): SparkKafkaProducer[K, V] = {
    val createProducerFunc = () => {
      val producer = new Producer[K, V](config)

      sys.addShutdownHook {
        //Ensure that, on executor JVM shutdown, the Kafka producer sends any buffered messages to Kafka before shutting down.
        producer.close;
      }

      producer
    }
    new SparkKafkaProducer(createProducerFunc)
  }

  def apply[K, V](config: java.util.Properties): SparkKafkaProducer[K, V] = apply(new ProducerConfig(config))

}
