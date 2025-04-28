package com.jungle.rocketmq.controller;

import com.jungle.rocketmq.mq.event.DelayCloseOrderEvent;
import com.jungle.rocketmq.mq.producer.DelayCloseOrderSendProducer;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test/rocketmq")
public class TestController {

    private final DelayCloseOrderSendProducer delayCloseOrderSendProducer;

    @GetMapping("/send")
    public SendResult sendDelayCloseOrder(String customName) {
        DelayCloseOrderEvent delayCloseOrderEvent = DelayCloseOrderEvent.builder()
                .orderSn(UUID.randomUUID().toString())
                .customName(customName)
                .build();
        return delayCloseOrderSendProducer.sendMessage(delayCloseOrderEvent);
    }
}
