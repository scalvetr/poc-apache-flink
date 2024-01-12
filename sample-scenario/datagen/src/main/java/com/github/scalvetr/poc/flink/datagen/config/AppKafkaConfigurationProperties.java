package com.github.scalvetr.poc.flink.datagen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app.kafka")
public record AppKafkaConfigurationProperties(String topic, int partitions, int replicas) {
}