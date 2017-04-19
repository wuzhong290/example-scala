package org.jacoco.examples.scala.kafka;

import com.google.common.base.Preconditions;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by wuzhong on 2017/4/18.
 */
public class KafkaProducer1 {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer1.class);
    public static final String METADATA_BROKER_LIST_KEY = "metadata.broker.list";
    public static final String SERIALIZER_CLASS_KEY = "serializer.class";
    public static final String SERIALIZER_CLASS_VALUE = "kafka.serializer.StringEncoder";

    private static KafkaProducer1 instance = null;

    private Producer producer;

    private KafkaProducer1(String brokerList) {

        Preconditions.checkArgument(StringUtils.isNotBlank(brokerList), "kafka brokerList is blank...");

        // set properties
        Properties properties = new Properties();
        properties.put(METADATA_BROKER_LIST_KEY, brokerList);
        properties.put(SERIALIZER_CLASS_KEY, SERIALIZER_CLASS_VALUE);
        properties.put("request.required.acks", "0");
        properties.put("topic.metadata.refresh.interval.ms", "60000");
        properties.put("producer.type", "sync");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        this.producer = new Producer(producerConfig);
    }

    public static synchronized KafkaProducer1 getInstance(String brokerList) {
        if (instance == null) {
            instance = new KafkaProducer1(brokerList);
            logger.info("初始化 kafka producer...");
            // Ensure that, on executor JVM shutdown, the Kafka producer sends any buffered messages to Kafka before shutting down.
            addShutdownHook(instance);
        }
        return instance;
    }

    // 单条发送
    public void send(KeyedMessage<String, String> keyedMessage) {
        producer.send(keyedMessage);
    }

    // 批量发送
    public void send(List<KeyedMessage<String, String>> keyedMessageList) {
        producer.send(keyedMessageList);
    }

    public void shutdown() {
        producer.close();
    }

    private static void addShutdownHook(final KafkaProducer1 producer) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    producer.shutdown();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
    }
}
