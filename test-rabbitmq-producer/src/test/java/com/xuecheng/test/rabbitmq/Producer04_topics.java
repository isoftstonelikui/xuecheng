package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer04_topics {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    private static final String ROUTINGKEY_EMAIL = "inform.#.email.#";
    private static final String ROUTINGKEY_SMS = "inform.#.sms.#";

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
            //声明队列，如果mq中没有此队列将自动创建
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //声明交换机
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC, true);
            //交换机和队列绑定
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_TOPICS_INFORM, ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_TOPICS_INFORM, ROUTINGKEY_SMS);
            //发布sms消息
/*            String sms = "send sms message";
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.sms", MessageProperties.PERSISTENT_TEXT_PLAIN, sms.getBytes());
            System.out.println(sms);*/
            //发布email消息
/*            String email = "send email message";
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.email", MessageProperties.PERSISTENT_TEXT_PLAIN, email.getBytes());
            System.out.println(email);*/
            //发布sms和email消息
/*            String smsEmail = "send sms and email message";
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.sms.email", MessageProperties.PERSISTENT_TEXT_PLAIN, smsEmail.getBytes());
            System.out.println(smsEmail);*/
            //测试*和#
            String test = "test通配符";
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform...email", MessageProperties.PERSISTENT_TEXT_PLAIN, test.getBytes());
            System.out.println(test);
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
