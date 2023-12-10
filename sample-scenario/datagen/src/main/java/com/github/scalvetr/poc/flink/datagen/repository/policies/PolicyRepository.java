package com.github.scalvetr.poc.flink.datagen.repository.policies;

import com.github.scalvetr.poc.flink.datagen.model.Policy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyRepository extends MongoRepository<Policy, String> {

}