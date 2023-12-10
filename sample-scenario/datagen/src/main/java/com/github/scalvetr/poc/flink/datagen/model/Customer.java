package com.github.scalvetr.poc.flink.datagen.model;

import java.util.List;

public record Customer(
        String customreId,
        String name,
        String surname,
        String email,
        List<Telephone> telephones,
        List<Address> addresses
) {
}
