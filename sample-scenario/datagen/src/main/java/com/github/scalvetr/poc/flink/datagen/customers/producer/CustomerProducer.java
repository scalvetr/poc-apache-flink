package com.github.scalvetr.poc.flink.datagen.customers.producer;

import com.github.scalvetr.poc.flink.datagen.customers.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerProducer {

    public Customer produceCustomer(Customer customer) {

        return customer;
    }
}
