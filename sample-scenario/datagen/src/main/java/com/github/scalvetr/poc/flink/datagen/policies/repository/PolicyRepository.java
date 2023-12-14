package com.github.scalvetr.poc.flink.datagen.policies.repository;

import com.github.scalvetr.poc.flink.datagen.policies.model.Policy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyRepository extends MongoRepository<Policy, String> {

}