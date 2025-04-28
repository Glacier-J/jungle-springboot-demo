package com.jungle.rocketmq.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 延迟关闭订单事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayCloseOrderEvent {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 自定义名称
     */
    private String customName;

    // 其他所需信息 ......

}
