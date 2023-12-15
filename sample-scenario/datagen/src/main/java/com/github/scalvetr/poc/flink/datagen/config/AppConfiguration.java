package com.github.scalvetr.poc.flink.datagen.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(value = AppKafkaConfigurationProperties.class)
public class AppConfiguration {
}