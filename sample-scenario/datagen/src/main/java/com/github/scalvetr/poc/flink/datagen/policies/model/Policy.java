package com.github.scalvetr.poc.flink.datagen.policies.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.OffsetDateTime;

@Document(collection = "policy")
public record Policy(
        @MongoId
        ObjectId id,
        String policyId,
        String customerId,
        String title,
        OffsetDateTime creationTimestamp) {
}
