package com.xuecheng.test.review;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @Auther: likui
 * @Date: 2019/9/20 19:48
 * @Description:
 */
public class Consumer02_subscribe_all {
    //队列名称
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";

    public static void main(String[] args) {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        //获得连接
        Connection connection = null;
        Channel channel_sms = null;
        Channel channel_email = null;
        try {
            connection = connectionFactory.newConnection();
            channel_sms = connection.createChannel();
            channel_email = connection.createChannel();
            //分别声明队列和交换机
            channel_sms.queueDeclare(QUEUE_INFORM_SMS, true,false ,false ,null );
            channel_sms.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT, true);
            channel_sms.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");

            channel_email.queueDeclare(QUEUE_INFORM_EMAIL, true,false ,false ,null );
            channel_email.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT, true);
            channel_email.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");
            //消费sms
            Consumer consumer_sms = new DefaultConsumer(channel_sms){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("consumerTag:"+consumerTag);
                    System.out.println("message:"+new String(body,"utf-8"));
                    System.out.println();
                }
            };
            channel_sms.basicConsume(QUEUE_INFORM_SMS, true, consumer_sms);
            //消费email
            Consumer consumer_email =new DefaultConsumer(channel_email){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("consumerTag:"+consumerTag);
                    System.out.println("message:"+new String(body,"utf-8"));
                    System.out.println();
                }
            } ;
            channel_email.basicConsume(QUEUE_INFORM_EMAIL, true,consumer_email );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
