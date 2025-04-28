package com.jungle.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.jungle.kafka.constant.KafkaConstant.TOPIC_JUNGLE_DEMO;

/**
 * kafka 消费者
 * 消息向指定主题发送消息，主题下的每个消费者组都可以消费（发布订阅），同一个消费者组中只有一个消费者消费（点对点）
 */
@Slf4j
@Component
public class DemoKafkaListener {

    /*
    在这种情况下：
        同一条消息会被不同消费者组(group1和group2)各自接收一次
        在同一个消费者组内(group1中的subscriber1和subscriber2)，消息只会被其中一个消费者接收
     */
    /**
     * 监听kafka数据
     * @param consumerRecords
     */
    @KafkaListener(topics = {TOPIC_JUNGLE_DEMO}, groupId = "group1")
    public void consumer1(ConsumerRecord<?, ?> consumerRecords) throws InterruptedException {
        log.info("【消费者1】 收到 kafka 推送的数据: {}", consumerRecords.toString());
        Thread.sleep(1000);
    }

    /**
     * 监听kafka数据
     *
     * @param consumerRecords
     */
    @KafkaListener(topics = {TOPIC_JUNGLE_DEMO}, groupId = "group2")
    public void consumer2(ConsumerRecord<?, ?> consumerRecords) throws InterruptedException {
        log.info("【消费者2】 收到 kafka 推送的数据: {}", consumerRecords.toString());
        Thread.sleep(1000);
    }

    /**
     * 监听kafka数据
     *
     * @param consumerRecords
     */
    @KafkaListener(topics = {TOPIC_JUNGLE_DEMO}, groupId = "group2")
    public void consumer3(ConsumerRecord<?, ?> consumerRecords) throws InterruptedException {
        log.info("【消费者3】 收到 kafka 推送的数据: {}", consumerRecords.toString());
        Thread.sleep(1000);
    }

//
//    /**
//     * 监听kafka数据（批量消费）
//     * @param consumerRecords
//     * @param ack
//     */
//    @KafkaListener(topics = {"big_data_topic"}, containerFactory = "batchFactory")
//    public void batchConsumer(List<ConsumerRecord<?, ?>> consumerRecords, Acknowledgment ack) {
//        long start = System.currentTimeMillis();
//
//        //...
//        //db.batchSave(consumerRecords);//批量插入或者批量更新数据
//
//        //手动提交
//        ack.acknowledge();
//        log.info("收到bigData推送的数据，拉取数据量：{}，消费时间：{}ms", consumerRecords.size(), (System.currentTimeMillis() - start));
//    }

}
