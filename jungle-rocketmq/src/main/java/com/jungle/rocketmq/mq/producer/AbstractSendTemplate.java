package com.jungle.rocketmq.mq.producer;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSendTemplate<T> {

    private final RocketMQTemplate rocketMQTemplate;


    /**
     * 构建消息发送事件所需基础扩充属性实体
     * @param messageSendEvent 消息发送事件
     * @return 扩充属性实体
     */
    protected abstract BaseSendExtendDTO buildBaseSendExtentDTO(T messageSendEvent);

    /**
     * 构建消息基本参数，请求头、keys...
     * @param messageSendEvent 消息发送事件
     * @param baseSendExtendDTO 扩充属性实体
     * @return 消息基本参数
     */
    protected abstract Message<?> buildMessage(T messageSendEvent, BaseSendExtendDTO baseSendExtendDTO);

    /**
     * 通用发送消息事件
     * @param messageSendEvent 消息发送事件
     * @return 消息发送返回结果
     */
    public SendResult sendMessage(T messageSendEvent){
        BaseSendExtendDTO baseSendExtendDTO = buildBaseSendExtentDTO(messageSendEvent);
        SendResult sendResult;
        try {
            //构建 destination( topic:tag )
            StringBuilder destinationSb = new StringBuilder();
            destinationSb.append(baseSendExtendDTO.getTopic());
            if(StringUtils.isNotBlank(baseSendExtendDTO.getTag())){
                destinationSb.append(":").append(baseSendExtendDTO.getTag());
            }
            SendResult sendResult1 = rocketMQTemplate.syncSend(destinationSb.toString(), buildMessage(messageSendEvent, baseSendExtendDTO));
            log.info("sendResult1:{}",sendResult1);
            sendResult = rocketMQTemplate.syncSend(
                    destinationSb.toString(),
                    buildMessage(messageSendEvent, baseSendExtendDTO),
                    baseSendExtendDTO.getSentTimeout(),
                    baseSendExtendDTO.getDelayLevel()
            );
            log.info("[{}] 消息发送结果：{}，消息ID：{}，消息Keys：{}", baseSendExtendDTO.getEventName(), sendResult.getSendStatus(), sendResult.getMsgId(), baseSendExtendDTO.getKeys());
        }catch (Throwable ex){
            log.error("[{}] 消息发送失败，消息体：{}", baseSendExtendDTO.getEventName(), JSON.toJSONString(messageSendEvent), ex);
            throw ex;
        }
        return sendResult;
    }

}
