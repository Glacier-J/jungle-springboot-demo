package com.jungle.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /* -------- 广播 ----------*/
    @Bean
    public FanoutExchange fanoutExchange01() {
        return new FanoutExchange("fanout.exchange.demo01");
    }

    @Bean
    public Queue queueDemo01() {
        return new Queue("queue.demo01", false); // 队列名, 是否持久化
    }

    @Bean
    public Binding bindingDemo01(Queue queueDemo01) {
        return BindingBuilder.bind(queueDemo01).to(fanoutExchange01());
    }

    /*----------------------------*/

    @Bean
    public DirectExchange directExchange01() {
        return new DirectExchange("direct.exchange.demo01");
    }

    @Bean
    public Queue queueDemo02() {
        return new Queue("queue.demo02", false); // 队列名, 是否持久化
    }

    @Bean
    public Binding bindingDemo02(Queue queueDemo02) {
        return BindingBuilder.bind(queueDemo02)
                .to(directExchange01())
                .with("routing.key.demo01");
    }


}
