package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * rabbitmq入门程序
 */
public class Consumer01_nack {
    private static final String QUEUE = "HelloWorld";

    public static void main(String[] args) {
        //连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        //获得连接和会话通道
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE, true, false, false, null);
            //定义消费方法
            Channel finalChannel = channel;
            DefaultConsumer consumer = new DefaultConsumer(finalChannel) {
                /**
                 *
                 * @param consumerTag 消费者标签，用来标识消费者的
                 * @param envelope    信封
                 * @param properties  消息属性
                 * @param body        消息内容
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //获取交换机
                    String exchange = envelope.getExchange();
                    //消息id,mq在channel中用于标识消息的id,可用于确认消息是否已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    //路由key
                    String key = envelope.getRoutingKey();
                    //消息内容
                    String message = new String(body, "utf-8");
                    System.out.println("message:" + message);
                    System.out.println("exchange:" + exchange);
                    System.out.println("deliveryTag:" + deliveryTag);
                    System.out.println("key:" + key);
                    System.out.println(consumerTag);
                    finalChannel.basicAck(deliveryTag, false);
                }
            };
            /**
             * String queue, boolean autoAck,Consumer callback
             * 队列名称
             * 是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置为false则需要手动回复
             * 消费方法，消费者接收到消息后调用此方法
             */
            channel.basicConsume(QUEUE, false, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
