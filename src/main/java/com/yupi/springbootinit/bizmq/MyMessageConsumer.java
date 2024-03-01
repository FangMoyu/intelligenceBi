package com.yupi.springbootinit.bizmq;


import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
// 使用@Slf4j注解生成日志记录器
@Slf4j
public class MyMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;
    // @SneakThrows 注解简化异常处理，
    // 底层实际上是给需要抛出检查型异常的方法加上了 try-catch，使用这个注解可以让代码更加简洁
    @SneakyThrows
    // rabbitMQ 消息监听注解，可以用来接收消息
    // 设置队列名称和消息确认模式为手动确认
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    // 在RabbitMQ中,每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序。
    // 通过使用@Header(AmqpHeaders.DELIVERY_TAG)注解,
    // 可以从消息头中提取出该投递标签,并将其赋值给long deliveryTag参数。
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        log.info("收到消息：{}" , message);
        channel.basicAck(deliveryTag, false);
    }
}
