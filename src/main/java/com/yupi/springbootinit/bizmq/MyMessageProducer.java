package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyMessageProducer {
    // 使用启动器自带的 rabbitTemplate，进行消息发送
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息，指定交换机，路由key，消息
     * @param exchange 交换机名称
     * @param routingKey 路由键
     * @param message 具体发送的消息
     */
    public void sendMessage(String exchange, String routingKey, String message) {
        // 外部做参数校验
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
