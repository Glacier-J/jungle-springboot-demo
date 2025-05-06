package com.jungle.rocketmq.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.jungle.rocketmq.mq.domain.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "demo01_topic",
        consumerGroup = "demo01_tag",
        //使用SQL表达式监听消息，更加灵活强大,发送消息时，通过headers 指定本消息条件并通过convertAndSend发送
        //MQ默认不支持SQL表达过滤，在broker.conf文件添加 enablePropertyFilter=true ，然后指定 broker.conf 文件启动
        selectorType = SelectorType.SQL92,
        selectorExpression = "orderId<10"
)
public class Demo01Consumer<T> implements RocketMQListener<MessageWrapper<T>> {

    @Override
    public void onMessage(MessageWrapper<T> messageWrapper) {
        log.info("[处理id小于10的订单] 开始消费：{}", JSON.toJSONString(messageWrapper));
    }
}


