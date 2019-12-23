package com.xuecheng.test.review;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class Consumer03_routing_sms {
    //队列名称
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    private static final String ROUTINGKEY_SMS = "inform_sms";

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
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //声明交换机
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT, true);
            //绑定交换机
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, "inform");
            //定义消费方法
            Channel finalChannel = channel;
            DefaultConsumer consumer = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    Action action = Action.ACCEPT;
                    long tag = envelope.getDeliveryTag();
                    try {
                        String message = new String(body, "utf-8");
                        System.out.println("message:" + message);
                        int j = 1 / 0;
                    } catch (Exception e) {
                        //根据错误种类来确定重试还是直接拒绝
                        e.printStackTrace();
                        action = Action.REJECT;
                    } finally {
                        try {
                            // 通过finally块来保证Ack/Nack会且只会执行一次
                            if (action == Action.ACCEPT) {
                                finalChannel.basicAck(tag, true);
                                // 重试，可以设置重试次数,超过一定此时则消息入库
                            } else if (action == Action.RETRY) {
                                finalChannel.basicNack(tag, false, true);
                                // 拒绝消息也相当于主动删除mq队列的消息
                            } else {
                                //finalChannel.basicNack(tag, false, false);
                                finalChannel.basicReject(tag, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            };

            channel.basicConsume(QUEUE_INFORM_SMS, false, consumer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
