package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicConsumer {
    private static final String EXCHANGE_NAME = "topic-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName1 = "frontend_queue";
        String queueName2 = "backend_queue";
        channel.queueDeclare(queueName1, false, false, false, null);
        channel.queueBind(queueName1, EXCHANGE_NAME, "#.frontend.#");
        channel.queueDeclare(queueName2, false, false, false, null);
        channel.queueBind(queueName2, EXCHANGE_NAME, "*.backend.*");
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("收到前端消息" + message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("收到后端消息" + message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        channel.basicConsume(queueName1, false, deliverCallback1, consumerTag -> {});
        channel.basicConsume(queueName2, false, deliverCallback2, consumerTag -> {});
    }
}
