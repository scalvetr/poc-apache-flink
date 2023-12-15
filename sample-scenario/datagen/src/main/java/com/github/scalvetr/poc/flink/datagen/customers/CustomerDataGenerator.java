package com.github.scalvetr.poc.flink.datagen.customers;

import com.github.scalvetr.poc.flink.customer.model.Address;
import com.github.scalvetr.poc.flink.customer.model.Customer;
import com.github.scalvetr.poc.flink.customer.model.Telephone;
import com.github.scalvetr.poc.flink.datagen.customers.producer.CustomerProducer;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CustomerDataGenerator {
    CustomerProducer repository;

    public CustomerDataGenerator(CustomerProducer repository) {
        this.repository = repository;
    }

    public Customer generateCustomer() {
        var faker = new Faker();

        var customerId = UUID.randomUUID().toString();
        var name = faker.name().fullName(); // Miss Samanta Schmidt
        var firstName = faker.name().firstName(); // Emory
        var email = name.toLowerCase() + "." + firstName.toLowerCase() + "@gmail.com"; // Barton


        return repository.produceCustomer(new Customer(
                customerId,
                name,
                firstName,
                email,
                List.of(
                        new Telephone(faker.phoneNumber().phoneNumber(), Boolean.TRUE),
                        new Telephone(faker.phoneNumber().phoneNumber(), Boolean.FALSE)
                ),
                List.of(buildAddress(faker.address(), Boolean.TRUE),
                        buildAddress(faker.address(), Boolean.FALSE))
        ));
    }

    private Address buildAddress(net.datafaker.providers.base.Address address, Boolean aTrue) {
        return new Address(
                address.streetAddress(),
                address.streetAddressNumber(),
                address.cityName(),
                address.country(),
                address.zipCode(), aTrue);
    }
}
