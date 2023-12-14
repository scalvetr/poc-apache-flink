package com.github.scalvetr.poc.flink.datagen.customers.model;

import java.util.List;

public record Customer(
        String customerId,
        String name,
        String surname,
        String email,
        List<Telephone> telephones,
        List<Address> addresses
) {
}
