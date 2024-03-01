package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BiMessageProducer {
    // 使用启动器自带的 rabbitTemplate，进行消息发送
    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final String BI_EXCHANGE = "bi_exchange";
    private static final String ROUTING_KEY = "bi_routingKey";
    /**
     * @param chartId 具体发送的消息,图表的 id
     */
    public void sendChartId(Long chartId) {
        // 外部做参数校验
        rabbitTemplate.convertAndSend(BI_EXCHANGE, ROUTING_KEY, chartId.toString());
    }
}
