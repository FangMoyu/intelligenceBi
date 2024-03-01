package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 *  一对一的情况下的消费者
 */
public class SingleConsumer {
    // 队列名称和要接收的队列名称一致
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws Exception {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 RabbitMQ 的 Host
        factory.setHost("localhost");
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 让消费者消费完一个消息后才能消费下一个
        channel.basicQos(1);
        // 声明队列,要和发布者的队列声明一致。
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 接收消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            // ack 消息
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        /**
         * 参数表示
         * 1. 队列名称
         * 2. 是否自动 ACK
         * 3. 消息回调参数，指定了接收到的消息的具体执行方案
         * 4， 空的一个 Lambda 表达式
         */
        channel.basicConsume(QUEUE_NAME, false, deliverCallback,  consumerTag -> { });
    }
}
