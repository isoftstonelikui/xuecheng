package com.xuecheng.test.review;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

/**
 * @Auther: likui
 * @Date: 2019/9/20 19:10
 * @Description:
 */
public class Producer03_routing {
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    private static final String ROUTINGKEY_EMAIL = "inform_email";
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

            //死信队列设置
            Map<String, Object> arguments = new HashMap<String, Object>(16);

            //死信队列配置  ----------------
            String dlxExchangeName = "dlx.exchange";
            String dlxQueueName = "dlx.queue";
            String informRoutingKey = "inform.#";

            // 为队列设置队列交换器
            arguments.put("x-dead-letter-exchange", dlxExchangeName);

            //声明交换机
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT, true);

            //声明队列，如果mq中没有此队列将自动创建
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, arguments);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //交换机和队列绑定
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, "inform");
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, "inform");

            // 创建死信交换器和队列
            channel.exchangeDeclare(dlxExchangeName, "topic", true);
            channel.queueDeclare(dlxQueueName, true, false, false, null);
            channel.queueBind(dlxQueueName, dlxExchangeName, informRoutingKey);


            //发送消息,异步编程确认，确保生产者消息正确到达broker
            String message = "inform message ";
            SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
            channel.confirmSelect();
            channel.addConfirmListener(new ConfirmListener() {
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    if (multiple) {
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }

                public void handleNack(long deliveryTag, boolean multiple) throws
                        IOException {
                    System.out.println("Nack, SeqNo: " + deliveryTag + ", multiple: " + multiple);
                    if (multiple) {
                        confirmSet.headSet(deliveryTag + 1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }
            });

            //while (true) {
            long nextSeqNo = channel.getNextPublishSeqNo();
            channel.basicPublish(EXCHANGE_ROUTING_INFORM, "inform", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
            confirmSet.add(nextSeqNo);
            //}

            //发送消息
//            String message = "inform message ";
//            channel.confirmSelect();
//            channel.basicPublish(EXCHANGE_ROUTING_INFORM, "inform", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("utf-8"));
//            boolean confirms = channel.waitForConfirms();
//            System.out.println(confirms);
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
