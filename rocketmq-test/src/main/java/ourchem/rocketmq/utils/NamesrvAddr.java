package ourchem.rocketmq.utils;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class NamesrvAddr {

    public static void getNamesrvAddr(DefaultMQProducer producer){
        producer.setNamesrvAddr("192.168.106.131:9876");
    }

    public static void getNamesrvAddr(DefaultMQPushConsumer consumer){
        consumer.setNamesrvAddr("192.168.106.131:9876");
    }

}
