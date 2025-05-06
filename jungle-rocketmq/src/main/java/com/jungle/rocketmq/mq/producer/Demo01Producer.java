package com.jungle.rocketmq.mq.producer;

import lombok.Data;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class Demo01Producer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage() {
        //创建订单消息
        Order order = new Order();
        order.setUserId(1);
        order.setOrderNo(UUID.randomUUID().toString());
        order.setPrice(500D);

        //通过header携带条件告知当前userId为1
        Map<String, Object> headers = new HashMap<>();
        headers.put("userId", 1);
        //生成消息
        Message<Order> message = MessageBuilder.withPayload(order).build();
        //发送
        rocketMQTemplate.convertAndSend("ORDER_ADD", message, headers);
    }

    @Data
    static class Order {
        private Integer userId;
        private String orderNo;
        private Double price;
    }

}
