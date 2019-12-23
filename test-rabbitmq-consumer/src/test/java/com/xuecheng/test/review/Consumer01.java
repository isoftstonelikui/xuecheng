package com.xuecheng.test.review;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Auther: likui
 * @Date: 2019/9/20 08:07
 * @Description:
 */
public class Consumer01 {
    private static final String QUEUE = "HelloWorld";

    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        //创建连接
        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            //声明队列
            channel.queueDeclare(QUEUE, true, false, false, null);
            //消费方法
            Consumer consumer = new DefaultConsumer(channel){
                //接收到消息后此方法被调用
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("consumerTag:"+consumerTag);
                    long deliveryTag = envelope.getDeliveryTag();
                    String exchange = envelope.getExchange();
                    String routingKey = envelope.getRoutingKey();
                    System.out.println("deliveryTag:"+deliveryTag);
                    System.out.println("exchange:"+exchange);
                    System.out.println("routingKey:"+routingKey);
                    System.out.println("message:"+new String(body,"utf-8"));
                }
            };
            //监听队列
            channel.basicConsume(QUEUE, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
