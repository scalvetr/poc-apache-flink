package com.github.scalvetr.poc.flink.datagen.config;

import com.github.scalvetr.poc.flink.datagen.repository.claims.ClaimRepository;
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
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = ClaimRepository.class, mongoTemplateRef = "mongoClaimsTemplate")
@EnableConfigurationProperties
public class MongoClaimsConfig {
    @Bean(name = "mongoClaimsProperties")
    @ConfigurationProperties(prefix = "mongodb.claims")
    @Primary
    public MongoProperties primaryProperties() {
        return new MongoProperties();
    }

    @Bean(name = "mongoClaimsClient")
    public MongoClient mongoClient(@Qualifier("mongoClaimsProperties") MongoProperties mongoProperties) {

        MongoCredential credential = MongoCredential
                .createCredential(mongoProperties.getUsername(), mongoProperties.getAuthenticationDatabase(), mongoProperties.getPassword());

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder
                        .hosts(Collections.singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .credential(credential)
                .build());
    }

    @Primary
    @Bean(name = "mongoClaimsDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(
            @Qualifier("mongoClaimsClient") MongoClient mongoClient,
            @Qualifier("mongoClaimsProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Primary
    @Bean(name = "mongoClaimsTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("mongoClaimsDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}