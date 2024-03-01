package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class SaveDataMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final String BI_EXCHANGE = "saveData_exchange";
    private static final String ROUTING_KEY = "saveData_routingKey";
    /**
     * @param copyWritingJson 对于数据库保存失败但是成功生成的数据，可以通过消息队列重新保存
     */
    public void SendUserInputSaveAgain(String copyWritingJson) {
        // 外部做参数校验
        rabbitTemplate.convertAndSend(BI_EXCHANGE, ROUTING_KEY, copyWritingJson);
    }
}
