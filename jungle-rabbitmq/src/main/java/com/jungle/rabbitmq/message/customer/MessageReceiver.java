package com.jungle.rabbitmq.message.customer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {


    @RabbitListener(queues = "queue.demo01")
    public void receive(String message) {
        System.out.println(" [x] Received '" + message + "'");
    }


}
