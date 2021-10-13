package ourchem.rocketmq.filter;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import ourchem.rocketmq.utils.NamesrvAddr;

import java.io.UnsupportedEncodingException;

public class FilterProducer{

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        NamesrvAddr.getNamesrvAddr(producer);
        producer.start();
        String tag = "TagA";
        for (int i = 0; i < 100; i++) {
            Message msg = new Message("FilterTopic",tag,
                    ("Filter Message " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            // 设置一些属性
            msg.putUserProperty("a", String.valueOf(i));
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult);
        }


        producer.shutdown();

    }

}