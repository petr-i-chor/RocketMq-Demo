package ourchem.rocketmq.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionListenerImpl implements TransactionListener {
    private AtomicInteger transactionIndex = new AtomicInteger(0);
    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

    // 回调操作方法
    // 消息预提交成功就会触发该方法的执行，用于完成本地事务
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

        System.out.println("预提交成功");

        int value = transactionIndex.getAndIncrement();
        int status = value % 3;
        localTrans.put(msg.getTransactionId(), status);
        return LocalTransactionState.UNKNOW;
    }


    // 消息回查方法
    // 引发消息回查的原因最常见的有两个：
    // 1)回调操作返回UNKNOW
    // 2)TC没有接收到TM的最终全局事务确认指令

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {

        Integer status = localTrans.get(msg.getTransactionId());
        if (null != status) {
            switch (status) {
                case 0:
                    System.out.println("执行消息回查" + msg.getTags());
                    return LocalTransactionState.UNKNOW;
                case 1:
                    System.out.println("执行消息全局确定指令" + msg.getTags());
                    return LocalTransactionState.COMMIT_MESSAGE;
                case 2:
                    System.out.println("执行消息全局返回指令" + msg.getTags());
                    return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}

