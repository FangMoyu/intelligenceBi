package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class FanoutProducer {
    private final static String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 RabbitMQ 的 Host
        factory.setHost("localhost");
        // 生成连接
        Connection connection = factory.newConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 创建 扇出 fanout 交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String message = scanner.next();
            // 发送消息到交换机
            /**
             * 参数介绍
             * 1，交换机名称
             * 2.routingKey，只有绑定了相同的 routingKey 的队列才能接收到该消息，不设置就表明任何的队列都可以接收到消息
             * 3.props，消息的属性
             */
            channel.basicPublish(EXCHANGE_NAME, "testA", null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
