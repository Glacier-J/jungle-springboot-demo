package com.jungle.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTTLConfig {

    // 普通队列名称
    public static final String NORMAL_QUEUE = "normal.queue";
    // 死信队列名称
    public static final String DLX_QUEUE = "dlx.queue";

    // 普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal.exchange";
    // 死信交换机名称
    public static final String DLX_EXCHANGE = "dlx.exchange";

    // 路由键
    public static final String ROUTING_KEY = "normal.routingkey";
    public static final String DLX_ROUTING_KEY = "dlx.routingkey";

    // 定义普通队列，设置TTL和死信交换
    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable(NORMAL_QUEUE)
                .ttl(60000)// 设置消息TTL为60秒(单位毫秒)
                .deadLetterExchange( DLX_EXCHANGE) // 设置死信交换机
                .deadLetterRoutingKey(DLX_ROUTING_KEY) // 设置死信路由键
                .build();
    }

    // 定义死信队列
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    // 定义普通交换机
    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange(NORMAL_EXCHANGE);
    }

    // 定义死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // 绑定普通队列到普通交换机
    @Bean
    public Binding normalBinding() {
        return BindingBuilder.bind(normalQueue())
                .to(normalExchange())
                .with(ROUTING_KEY);
    }

    // 绑定死信队列到死信交换机
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }
}
