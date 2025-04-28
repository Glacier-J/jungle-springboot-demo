package com.jungle.rocketmq.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.jungle.rocketmq.mq.domain.MessageWrapper;
import com.jungle.rocketmq.mq.event.DelayCloseOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.jungle.rocketmq.constant.RocketMQConstant.*;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = ORDER_DELAY_CLOSE_TOPIC_KEY,
        selectorExpression = ORDER_DELAY_CLOSE_TAG_KEY,
        consumerGroup = ORDER_DELAY_CLOSE_CG_KEY
)
public class DelayCloseOrderConsumer  implements RocketMQListener<MessageWrapper<DelayCloseOrderEvent>> {

    @Override
    public void onMessage(MessageWrapper<DelayCloseOrderEvent> delayCloseOrderEventMessageWrapper) {
        log.info("[延迟关闭订单] 开始消费：{}", JSON.toJSONString(delayCloseOrderEventMessageWrapper));
        DelayCloseOrderEvent delayCloseOrderEvent = delayCloseOrderEventMessageWrapper.getMessage();
    }
}
