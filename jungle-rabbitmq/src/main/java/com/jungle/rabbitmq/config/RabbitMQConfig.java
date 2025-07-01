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

    /*------------- 直连---------------*/

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
    //-----  Topic  ------
    // Topic（主题）和 Direct(直连) 交换机主要的区别在于topic支持通配符路由到队列中
    @Bean
    public Queue queueDemo03(){
        return new Queue("queue.demo03");
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topic.exchange.demo01");
    }

    @Bean
    public Binding bindingDemo03(){
        return BindingBuilder.bind(queueDemo03())
                .to(topicExchange())
                .with("routing.key.demo03.#");
    }


}
