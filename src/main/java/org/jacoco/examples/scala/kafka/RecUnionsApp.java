package org.jacoco.examples.scala.kafka;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.*;
/**
 * Created by dell on 2017/2/15.
 */
public final class RecUnionsApp {
    private static final Logger logger = LoggerFactory.getLogger(RecUnionsApp.class);
    private final static Random random = new Random();

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: RecUnionsApp <brokers> <topics> groupId\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <topics> is a list of one or more kafka topics to consume from\n" +
                    "  groupId is kafka groupId\n" +
                    "  duration is The time interval at which streaming data will be divided into batches\n\n");
            System.exit(1);
        }

        String brokers = args[0];
        String sourceTopics = args[1];
        String groupId = args[2];
        String destTopics = args[3];
        String duration = args[4];
        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf().setAppName("RecUnionsApp");//.setMaster("local[2]");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(Long.valueOf(duration)));
        final Broadcast<String> topicBroadcast = jssc.sparkContext().broadcast(destTopics);
        // set properties
        Properties properties = new Properties();
        properties.put("metadata.broker.list", brokers);
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "0");
        properties.put("topic.metadata.refresh.interval.ms", "60000");
        properties.put("producer.type", "sync");
        ProducerConfig producerConfig = new ProducerConfig(properties);
        SparkKafkaProducer<String,String> producer = SparkKafkaProducer.apply(producerConfig);
        final Broadcast<SparkKafkaProducer<String, String>> producerBroadcast = jssc.sparkContext().broadcast(producer);

        Set<String> topicsSet = new HashSet<String>(Arrays.asList(sourceTopics.split(",")));
        Map<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", brokers);
        kafkaParams.put("group.id", groupId);
        //最大日志 100M
        kafkaParams.put("fetch.message.max.bytes", "104857600");
        // Create direct kafka stream with brokers and topics
        JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topicsSet
        );

        JavaDStream<Tuple2<String, String>> msgDStreanm = messages.mapPartitions(new FlatMapFunction<Iterator<Tuple2<String, String>>, Tuple2<String, String>>() {
            @Override
            public Iterable<Tuple2<String, String>> call(Iterator<Tuple2<String, String>> tuple2Iterator) throws Exception {
                List<Tuple2<String, String>> messageList = new ArrayList<>();
                while (tuple2Iterator.hasNext()) {
                    Tuple2<String, String> tuple2 = tuple2Iterator.next();
                    try {
                        Map<String,Object> mes = JSON.parseObject(tuple2._2(), HashMap.class);
                        if (null == mes) {
                            continue;
                        }
                        String op = (String) mes.get("op");
                        String key = op + random.nextLong();
                        messageList.add(new Tuple2(key, tuple2._2()));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                return messageList;
            }
        });
        sendMesToKafka(msgDStreanm,producerBroadcast,topicBroadcast);
        addShutdownHook(jssc);
        //Start the computation
        jssc.start();
        jssc.awaitTermination();
    }
    private static void sendMesToKafka(JavaDStream<Tuple2<String, String>> msgDStreanm,final Broadcast<SparkKafkaProducer<String, String>> producerBroadcast,final Broadcast<String> topicBroadcast){
        msgDStreanm.foreachRDD(new Function<JavaRDD<Tuple2<String, String>>, Void>() {
            @Override
            public Void call(JavaRDD<Tuple2<String, String>> v1) throws Exception {
                v1.foreachPartition(new VoidFunction<Iterator<Tuple2<String, String>>>() {
                    @Override
                    public void call(Iterator<Tuple2<String, String>> tuple2Iterator) throws Exception {
                        SparkKafkaProducer<String,String> kafkaProducer =  producerBroadcast.getValue();

                        List<KeyedMessage<String, String>> messageList = Lists.newArrayList();
                        while (tuple2Iterator.hasNext()) {
                            Tuple2<String, String> tuple = tuple2Iterator.next();
                            messageList.add(new KeyedMessage<String, String>(topicBroadcast.getValue(),tuple._1(), tuple._2()));
                        }
                        kafkaProducer.send(messageList);
                    }
                });
                return null;
            }
        });

    }

    private static void addShutdownHook(final JavaStreamingContext streamingContext) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    streamingContext.stop(true, true);
                } catch (Exception e) {
                    // donothing
                }
            }
        });
    }
}

