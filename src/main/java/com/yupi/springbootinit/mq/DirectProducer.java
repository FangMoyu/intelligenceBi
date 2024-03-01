package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class DirectProducer {

  private static final String EXCHANGE_NAME = "direct_exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    // 从工厂创建连接
    try (Connection connection = factory.newConnection();
         // 创建通道
         Channel channel = connection.createChannel()) {
        // 声明交换机类型为 direct
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "xiaofang", null,
                    message.getBytes("UTF-8"));
            System.out.println(" [x] 发送给小方的消息" + message + ":");
        }

    }
  }
}