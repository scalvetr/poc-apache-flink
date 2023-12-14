package com.github.scalvetr.poc.flink.datagen.claims;

import com.github.scalvetr.poc.flink.datagen.claims.model.Claim;
import com.github.scalvetr.poc.flink.datagen.claims.repository.ClaimRepository;
import com.github.scalvetr.poc.flink.datagen.customers.model.Customer;
import net.datafaker.Faker;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ClaimDataGenerator {
    ClaimRepository repository;

    public ClaimDataGenerator(ClaimRepository repository) {
        this.repository = repository;
    }

    public Claim generateClaim(String customerId, String policyId) {
        var faker = new Faker();
        var claimId = UUID.randomUUID().toString();
        var title = faker.book().title();
        var creationTimestamp = OffsetDateTime.now();
        var result = new Claim(null, claimId, customerId, policyId, title, creationTimestamp);
        repository.insert(result);
        return result;
    }
}
