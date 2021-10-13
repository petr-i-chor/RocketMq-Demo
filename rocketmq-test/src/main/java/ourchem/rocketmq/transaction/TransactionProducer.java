package ourchem.rocketmq.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import ourchem.rocketmq.utils.NamesrvAddr;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

public class TransactionProducer {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        TransactionListener transactionListener = new TransactionListenerImpl();
        TransactionMQProducer producer = new TransactionMQProducer("please_rename_unique_group_name");
        NamesrvAddr.getNamesrvAddr(producer);

        /**
         * 定义一个线程池
         * @param corePoolSize 线程池中核心线程数量
         * @param maximumPoolSize 线程池中最多线程数
         * @param keepAliveTime 这是一个时间。当线程池中线程数量大于核心线程数量，多余空闲线程的存活时长
         * @param unit 时间单位
         * @param workQueue 临时存放任务的队列，其参数就是队列的长度
         * @param threadFactory 线程工厂
         */
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });

        // 为生产者指定一个线程池
        producer.setExecutorService(executorService);
        // 为生产者添加事务监听器
        producer.setTransactionListener(transactionListener);
        producer.start();
        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 5; i++) {
            try {
                Message msg =
                        new Message("TopicTransaction", tags[i % tags.length], "KEY" + i,
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                // 发送事务消息
                // tags[i]用于指定在执行本地事务时要使用的业务参数

                SendResult sendResult = producer.sendMessageInTransaction(msg, null);//发送事务消息
//                System.out.printf("%s%n", sendResult);
                Thread.sleep(3);
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();
    }
}

