package com.jungle.rocketmq.mq.producer;

import com.jungle.rocketmq.mq.domain.MessageWrapper;
import com.jungle.rocketmq.mq.event.DelayCloseOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.jungle.rocketmq.constant.RocketMQConstant.ORDER_DELAY_CLOSE_TAG_KEY;
import static com.jungle.rocketmq.constant.RocketMQConstant.ORDER_DELAY_CLOSE_TOPIC_KEY;

@Slf4j
@Component
public class DelayCloseOrderSendProducer extends AbstractSendTemplate<DelayCloseOrderEvent> {

    private final ConfigurableEnvironment environment;

    public DelayCloseOrderSendProducer(@Autowired RocketMQTemplate rocketMQTemplate, @Autowired ConfigurableEnvironment environment) {
        super(rocketMQTemplate);
        this.environment = environment;
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtentDTO(DelayCloseOrderEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("延时关闭订单")
                .keys(messageSendEvent.getOrderSn())
                .topic(environment.resolvePlaceholders(ORDER_DELAY_CLOSE_TOPIC_KEY))
                .tag(environment.resolvePlaceholders(ORDER_DELAY_CLOSE_TAG_KEY))
                .sentTimeout(2000L)
                // RocketMQ 延时10分钟
                // 延迟消息级别 1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
                // 对应延时时间 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
                .delayLevel(14)
                .build();
    }

    @Override
    protected Message<?> buildMessage(DelayCloseOrderEvent messageSendEvent, BaseSendExtendDTO baseSendExtendDTO) {
        String keys = StringUtils.isNotBlank(baseSendExtendDTO.getKeys()) ? baseSendExtendDTO.getKeys() : UUID.randomUUID().toString();

        return MessageBuilder
                .withPayload(new MessageWrapper<>(baseSendExtendDTO.getKeys(), messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, baseSendExtendDTO.getTag())
                .build();
    }
}
