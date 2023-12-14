package com.github.scalvetr.poc.flink.datagen.policies;

import com.github.scalvetr.poc.flink.datagen.policies.model.Policy;
import com.github.scalvetr.poc.flink.datagen.policies.repository.PolicyRepository;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class PolicyDataGenerator {
    PolicyRepository repository;

    public PolicyDataGenerator(PolicyRepository repository) {
        this.repository = repository;
    }

    public Policy generatePolicy(String customerId) {
        var faker = new Faker();
        var policyId = UUID.randomUUID().toString();
        var title = faker.book().title();
        var creationTimestamp = OffsetDateTime.now();
        var result = new Policy(null, policyId, customerId, title, creationTimestamp);
        repository.insert(result);
        return result;
    }
}
