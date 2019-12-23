package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq入门程序
 */
public class Producer01 {
    private static final String QUEUE = "HelloWorld";

    public static void main(String[] args) {
        //连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个MQ可以设置多个虚拟机，每个虚拟机相当于一个独立的MQ
        connectionFactory.setVirtualHost("/");
        //获得连接和会话通道
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            /**
             * 声明队列，如果mq中没有此队列将自动创建
             * 参数说明：
             * 队列名称
             * 是否持久化，如果持久化，mq重启后队列还在
             * 队列是否独占此连接，队列是否只允许在此连接中访问，连接关闭后队列自动删除
             * 队列不再使用时是否自动删除此队列
             * 队列参数，可用于扩展参数设置
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            /**
             * 发布消息
             * 参数说明：
             * 交换机，如不指定，将使用默认交换机
             * routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * 消息包含的属性
             * 消息体
             */
            String message = "你好 java";
            //测试事务
/*            try {
                channel.txSelect();
                channel.basicPublish("", QUEUE, null, message.getBytes());
                int i = 1 / 0;
                channel.txCommit();
            } catch (Exception e) {
                channel.txRollback();
                e.printStackTrace();
            }*/
            //设置channel为confirm模式
/*            channel.confirmSelect();
            channel.basicPublish("", QUEUE, null, message.getBytes());
            if (!channel.waitForConfirms()) {
                System.out.println("生产者消息发送失败");
            }*/
            //异步confirm模式：提供一个回调方法，服务端confirm了一条或者多条消息后Client端会回调这个方法
            SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
            channel.confirmSelect();
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    if (multiple) {
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("Nack, SeqNo: " + deliveryTag + ", multiple: " + multiple);
                    if (multiple) {
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }

            });
/*            while (true) {
                long nextSeqNo = channel.getNextPublishSeqNo();
                channel.basicPublish("", QUEUE, null, message.getBytes());
                confirmSet.add(nextSeqNo);
            }*/
            channel.basicPublish("", QUEUE, null, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
