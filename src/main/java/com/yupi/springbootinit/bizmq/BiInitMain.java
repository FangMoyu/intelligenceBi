package com.yupi.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化 MQ ，创建相应的测试队列
 */
@Slf4j
public class BiInitMain {
    @SneakyThrows
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 消息设置一个过期时间，免得部分消息未消费完毕一直调用接口
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("x-message-ttl", 120000);
            // 队列名称
            String EXCHANGE_NAME = "bi_exchange";
            // 声明交换机,指定交换机名称和类型
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            String queueName = "bi_queue";
            // 声明队列
            channel.queueDeclare(queueName, true, false, false, props);
            // 绑定交换机
            channel.queueBind(queueName, EXCHANGE_NAME, "bi_routingKey");
        }catch (Exception e) {
            log.info("初始化 MQ 失败:{}" , e);
        }
    }
}
