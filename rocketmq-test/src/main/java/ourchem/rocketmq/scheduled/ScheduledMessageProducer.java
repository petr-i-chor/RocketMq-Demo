package ourchem.rocketmq.scheduled;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import ourchem.rocketmq.utils.NamesrvAddr;

public class ScheduledMessageProducer {
    public static void main(String[] args) throws Exception {
        // 实例化一个生产者来产生延时消息
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        // 启动生产者
        NamesrvAddr.getNamesrvAddr(producer);
        producer.start();
        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("ScheduledMessage", ("Hello scheduled message " + i).getBytes());
            // 设置延时等级3,这个消息将在10s之后发送(现在只支持固定的几个时间,详看delayTimeLevel)
            message.setDelayTimeLevel(3);
            // 发送消息
            SendResult sendResult = producer.send(message);

            System.out.println(String.format("SendResult status:%s, queueId:%d, sendResult:%s",
                    sendResult.getSendStatus(),
                    sendResult.getMessageQueue().getQueueId(),
                    sendResult));
        }
        // 关闭生产者
        producer.shutdown();
    }
}
