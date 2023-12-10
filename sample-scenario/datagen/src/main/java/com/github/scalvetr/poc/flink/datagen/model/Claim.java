package com.github.scalvetr.poc.flink.datagen.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Document(collection = "claim")
public record Claim(
        @MongoId
        ObjectId id,
        String claimId,
        String customerId,
        String policyId,
        String title,
        OffsetDateTime creationTimestamp
) {
}
