package ourchem.rocketmq.listSplitter;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import ourchem.rocketmq.utils.NamesrvAddr;

import java.util.List;

public class BatchConsumer {
    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("cg");
        NamesrvAddr.getNamesrvAddr(consumer);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("splitterTopic", "*");
// 指定每次可以消费10条消息，默认为1
        consumer.setConsumeMessageBatchMaxSize(10);
// 指定每次可以从Broker拉取40条消息，默认为32
        consumer.setPullBatchSize(40);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus
            consumeMessage(List<MessageExt> msgs,
                           ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(msg);
                }

// 消费成功的返回结果
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
// 消费异常时的返回结果
// return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        System.out.println("Consumer Started");
    }
}
