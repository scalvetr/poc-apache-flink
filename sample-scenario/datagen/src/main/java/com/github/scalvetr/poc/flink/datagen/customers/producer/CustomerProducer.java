package com.github.scalvetr.poc.flink.datagen.customers.producer;

import com.github.scalvetr.poc.flink.customer.model.Customer;
import com.github.scalvetr.poc.flink.datagen.config.AppKafkaConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class CustomerProducer {
    private static final Logger log = LoggerFactory.getLogger(CustomerProducer.class);
    private final KafkaTemplate<String, Customer> kafkaTemplate;
    private final AppKafkaConfigurationProperties appKafkaConfigurationProperties;

    public CustomerProducer(KafkaTemplate<String, Customer> kafkaTemplate, AppKafkaConfigurationProperties appKafkaConfigurationProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.appKafkaConfigurationProperties = appKafkaConfigurationProperties;
    }

    public Customer produceCustomer(Customer customer) {
        log.debug("Customer {}", customer);
        kafkaTemplate.send(appKafkaConfigurationProperties.topic(), customer);
        return customer;
    }
}
