package com.jungle.rocketmq.constant;

public interface RocketMQConstant {

    /**
     * 订单服务相关业务 Topic Key
     */
//    String ORDER_DELAY_CLOSE_TOPIC_KEY = "${spring.application.name}_delay-close-order_topic";
    String ORDER_DELAY_CLOSE_TOPIC_KEY = "jungle-demo-service_delay-close-order_topic";

    /**
     * 购票服务创建订单后延时关闭业务 Tag Key
     */
    String ORDER_DELAY_CLOSE_TAG_KEY = "jungle-demo-service_delay-close-order_tag";

    /**
     * 延时关闭订单消费者组 cg
     */
    String ORDER_DELAY_CLOSE_CG_KEY = "jungle-demo-service_delay-close-order_cg";
}
