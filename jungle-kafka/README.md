[ymlbashspring-kafka官方文档](https://docs.spring.io/spring-kafka/docs/2.8.11/reference/html/)

下载安装：

https://www.apache.org/dyn/closer.cgi?path=/kafka/2.7.0/kafka_2.13-2.7.0.tgz

解压

 tar -xzf kafka_2.13-2.7.0.tgz

重命名

mv kafka_2.13-2.7.0 kafka2.7.0

启动zookeeper

```
zookeeper-server-start.sh config/zookeeper.properties
```

启动kafka

```
bin/kafka-server-start.sh config/server.properties
```

使用Docker部署

[https://hub.docker.com/r/bitnami/kafka](https://)

docker部署文档：[https://github.com/bitnami/containers/blob/main/bitnami/kafka](https://)

<br/>

修改/config/server.properties配置

```
#监听所有网络接口
listeners=PLAINTEXT://0.0.0.0:9092

#对外暴露的地址
advertised.listeners=PLAINTEXT://47.109.52.29:9092
```

<br/>

创建主题

```
kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
```

发送消息

```
bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
```

接收消息

```
bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```

Springboot整合Kafka

依赖：

```xml
<!--kafka-->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

```yaml
#yml配置
spring:
  kafka:
    # Broker 地址（集群用逗号分隔）
    bootstrap-servers: 47.109.52.29:9092
    producer:
      retries: 3                   # 重试次数
      batch-size: 1000             # 批量发送消息数
      buffer-memory: 33554432      # 缓冲区大小（32MB）
    consumer:
      group-id: "jungle-demo-service"  # 消费者组
      auto-offset-reset: earliest  # 从最早偏移量开始消费
      max-poll-records: 4000       # 单次拉取最大消息数
      enable-auto-commit: true      # 启用自动提交
      auto-commit-interval: 1000   # 自动提交间隔（ms）
      #批消费并发量，小于或等于Topic的分区数
      batch:
        concurrency: 3

```


总结
    · 点对点模式：通过让所有消费者使用相同的group.id实现
    · 发布订阅模式：通过让不同消费者使用不同的group.id实现
    · 关键区别在于消费者组的配置，生产者配置是相同的
