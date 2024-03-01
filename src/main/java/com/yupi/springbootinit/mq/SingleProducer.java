package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 消息队列测试
 */
public class SingleProducer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 rabbitMQ 的服务地址
        factory.setHost("localhost");
        // 获取连接对象
        Connection connection = factory.newConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        // 声明队列
        /**
         * 参数介绍
         * 1.队列名称
         * 2.队列是否持久化
         * 3.是否是排他性队列 (只允许首次声明它的连接 (Connection) 可见，其他用户都不可访问)
         * 4.是否自动删除 (当没有生产者或者消费者使用此队列，该队列会自动删除)
         * 5.队列的其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            // 任意字符串作为 message
            String message = scanner.next();
            // 发布消息
            /**
             * 参数介绍
             * 1.交换机，exchange (暂无，空字符串就是不指定)
             * 2.队列名称 QUEUE_NAME
             * 3.props 消息的属性
             * 4. 消息的内容
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }

    }
}
