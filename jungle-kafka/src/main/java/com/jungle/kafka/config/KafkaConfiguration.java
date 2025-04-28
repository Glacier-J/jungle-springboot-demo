package com.jungle.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jungle.kafka.constant.KafkaConstant.TOPIC_JUNGLE_DEMO;

/**
 * Kafka 配置类
 * 配置生产和消费拦截器 <a href="https://docs.spring.io/spring-kafka/docs/2.8.11/reference/html/#interceptors"/>
 */
@Slf4j
@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 创建主题
     *
     * @return
     */
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(TOPIC_JUNGLE_DEMO)
                .partitions(10)
                .replicas(1)
                .build();
    }


    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(createProducerConfig());
    }

    /**
     * 生产者配置信息
     *
     * @return
     */
    private Map<String, Object> createProducerConfig() {
        Map<String, Object> producerConfigProperties = new HashMap<>();
        producerConfigProperties.put(ProducerConfig.ACKS_CONFIG, "0");
        producerConfigProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        producerConfigProperties.put(ProducerConfig.RETRIES_CONFIG, retries);
//        producerConfigProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        producerConfigProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//        producerConfigProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        producerConfigProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return producerConfigProperties;
    }

    //------------------- 消费者 ---------------------

    @Bean
    public ConsumerFactory<?, ?> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(createConsumerConfig());
    }

    /**
     * 消费者配置信息
     *
     * @return
     */
    private Map<String, Object> createConsumerConfig() {
        Map<String, Object> consumerConfigProperties = new HashMap<>();
        consumerConfigProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerConfigProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        consumerConfigProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        consumerConfigProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//        consumerConfigProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        consumerConfigProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        consumerConfigProperties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        consumerConfigProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return consumerConfigProperties;
    }

    /**
     * 消费者批量工厂
     */
//    @Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<?, ?> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(createConsumerConfig()));
        //设置并发量，小于或等于Topic的分区数
//        factory.setConcurrency(batchConcurrency);
        factory.getContainerProperties().setPollTimeout(1500);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        return factory;
    }

    /**
     * 项目启动后发送消息
     * @param template
     * @return
     */
    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, Object> template) {
        return args -> {
            for (int i = 0; i < 7; i++) {
                System.out.println("---------------------------发送消息：" + i);
                Thread.sleep(3000);
                Map<String, Object> message = new LinkedHashMap<>();
                message.put("userId", i);
                message.put("userName", "Jungle");

                final ProducerRecord<String, Object> record = createRecord(message, i);
                log.info("向 kafka 发送消息：{}" ,record);
                //向kafka的big_data_topic主题推送数据
                template.send(record);
            }
        };
    }

    private static ProducerRecord<String, Object> createRecord(Map<String, Object> message,int partition) {
        return new ProducerRecord<>(
                TOPIC_JUNGLE_DEMO,
                1, //指定分区号，分区内的消息保证有序性,同一个分区的消息只能有一个消费者消费
                "key1", //可根据key的哈希值生成分区号
                message);
    }

}
