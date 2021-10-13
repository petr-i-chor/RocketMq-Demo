package ourchem.rocketmq.listSplitter;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import ourchem.rocketmq.utils.NamesrvAddr;

import java.util.ArrayList;
import java.util.List;

public class BatchProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        NamesrvAddr.getNamesrvAddr(producer);
// 指定要发送的消息的最大大小，默认是4M
// 不过，仅修改该属性是不行的，还需要同时修改broker加载的配置文件中的
// maxMessageSize属性
// producer.setMaxMessageSize(8 * 1024 * 1024);
        producer.start();
// 定义要发送的消息集合
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            byte[] body = ("Hi," + i).getBytes();
            Message msg = new Message("splitterTopic", "someTag", body);
            messages.add(msg);
        }
// 定义消息列表分割器，将消息列表分割为多个不超出4M大小的小列表
        MessageListSplitter splitter = new MessageListSplitter(messages);
        while (splitter.hasNext()) {
            try {
                List<Message> listItem = splitter.next();
                SendResult sendResult = producer.send(listItem);
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }
}