package com.yupi.springbootinit.mq;

import com.rabbitmq.client.*;


/**
 * RPC 实现原理
 * 没有完全固定的消费者和生产者，服务提供者既是消费者又是生产者
 * 生产者发送请求消息，消费者接收请求消息并处理，然后将结果返回给请求方
 * 请求方等待接收结果
 */
public class RPCServer {
    //队列名称
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    // 定义斐波那契函数，提供服务
    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);

        channel.basicQos(1);

        System.out.println(" [x] Awaiting RPC requests");
        // 从客户端(消费者)处收到调用方法的消息，编写回调函数，处理消费者请求，目标是调用斐波那契函数返回结果
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // 回调参数,从客户端设置好的回调参数中取出响应的 corrId(是唯一的，因为客户端调用的每个方法都统一使用同一个回调队列)
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            // 响应结果
            String response = "";
            try {
                // 获取客户端的消息
                String message = new String(delivery.getBody(), "UTF-8");
                int n = Integer.parseInt(message);
                // 调用函数
                System.out.println(" [.] fib(" + message + ")");
                response += fib(n);
            } catch (RuntimeException e) {
                System.out.println(" [.] " + e);
            } finally {
                // 发送消息到回调队列,这个 routingKey 参数实际上是队列名称参数
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));
    }
}