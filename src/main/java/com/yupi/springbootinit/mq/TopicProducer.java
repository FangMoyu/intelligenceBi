package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class TopicProducer {
    // 定义交换机名称为"direct-exchange"
    private static final String EXCHANGE_NAME = "topic-exchange";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        // 设置消息队列的主机地址为本地主机
        factory.setHost("localhost");
        // 创建连接和通道，并确保资源自动关闭
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明使用主题交换机，并指定交换机名称和类型
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String routingKey = "AAA";
            String message = "阿巴巴";
                // 发布消息到指定的交换机和路由键 
            for(int i = 0; i < 1000000; i++) {
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println("发送了:" + i + "次消息");
            }

                // 打印消息发送的提示信息，包含消息内容和路由键
            System.out.println(" [x] Sent '" + message + " with routing:" + routingKey + "'");

        }
    }
    //..
}