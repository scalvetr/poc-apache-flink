package com.github.scalvetr.poc.flink.datagen.config;

import com.github.scalvetr.poc.flink.datagen.repository.policies.PolicyRepository;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = PolicyRepository.class, mongoTemplateRef = "mongoPoliciesTemplate")
@EnableConfigurationProperties
public class MongoPolicyConfig {

    @Bean(name = "mongoPoliciesProperties")
    @ConfigurationProperties(prefix = "mongodb.policies")
    public MongoProperties secondaryProperties() {
        return new MongoProperties();
    }

    @Bean(name = "mongoPoliciesClient")
    public MongoClient mongoClient(@Qualifier("mongoPoliciesProperties") MongoProperties mongoProperties) {

        MongoCredential credential = MongoCredential
                .createCredential(mongoProperties.getUsername(), mongoProperties.getAuthenticationDatabase(), mongoProperties.getPassword());

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(Collections.singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .credential(credential)
                .build());
    }

    @Bean(name = "mongoPoliciesDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("mongoPoliciesClient") MongoClient mongoClient,
            @Qualifier("mongoPoliciesProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Bean(name = "mongoPoliciesTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("mongoPoliciesDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}