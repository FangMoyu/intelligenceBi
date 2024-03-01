package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutConsumer {
    // 交换机名称
    private final static String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME , "fanout");
        // 随机生成队列名称并声明队列,fanout 的发布订阅模式对于具体的某一个消费者情况是可以不在乎的，因此随机的名称也是可以的
        String queueName = channel.queueDeclare().getQueue();
        /**
         * 绑定交换机
         * 参数介绍
         * 1.队列名称
         * 2.交换机名称
         * 3.routingKey，路由键,如果设置为空字符串，则任何发送到该交换机的消息都会被路由到该队列
         * 如果设置成其他的字符串，那么发送到该交换机的消息，只有routingKey与该字符串相匹配的消息才会被路由到该队列
         */
        channel.queueBind(queueName, EXCHANGE_NAME, "testA" );
        // 当然，你也可以指定一个队列名称，便于生产者和消费者进行管理
        channel.queueDeclare("小方的专属消息队列", false, false, false, null);
        // 绑定第二个队列到相同的交换机
        channel.queueBind("小方的专属消息队列", EXCHANGE_NAME, "testB");

        // 消息回调1
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [随机名称队列] Received '" + message + "'");
        };
        // 消息回调2
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [小方队列] Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback1, consumerTag -> {});
        channel.basicConsume("小方的专属消息队列", true, deliverCallback2, consumerTag -> {});
    }
}
