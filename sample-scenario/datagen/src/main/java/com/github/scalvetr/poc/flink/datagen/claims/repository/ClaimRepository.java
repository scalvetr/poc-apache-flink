package com.github.scalvetr.poc.flink.datagen.claims.repository;

import com.github.scalvetr.poc.flink.datagen.claims.model.Claim;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClaimRepository extends MongoRepository<Claim, String> {

}