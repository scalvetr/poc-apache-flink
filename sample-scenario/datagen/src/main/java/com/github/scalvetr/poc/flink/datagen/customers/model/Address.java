package com.github.scalvetr.poc.flink.datagen.customers.model;

public record Address(
        String street,
        String number,
        String city,
        String country,
        String zipCode,
        Boolean _default) {
}
