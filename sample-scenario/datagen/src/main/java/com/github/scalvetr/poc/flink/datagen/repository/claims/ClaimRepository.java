package com.github.scalvetr.poc.flink.datagen.repository.claims;

import com.github.scalvetr.poc.flink.datagen.model.Claim;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClaimRepository extends MongoRepository<Claim, String> {

}