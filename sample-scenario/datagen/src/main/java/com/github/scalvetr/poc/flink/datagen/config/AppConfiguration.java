package com.github.scalvetr.poc.flink.datagen.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
@EnableConfigurationProperties(value = AppKafkaConfigurationProperties.class)
public class AppConfiguration {

    @Bean
    public NewTopic topicExample(AppKafkaConfigurationProperties properties) {
        return TopicBuilder.name(properties.topic())
                .partitions(properties.partitions())
                .replicas(properties.replicas())
                .build();
    }
}