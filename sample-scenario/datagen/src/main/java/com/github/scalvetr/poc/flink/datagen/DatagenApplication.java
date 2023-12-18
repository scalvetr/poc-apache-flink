package com.github.scalvetr.poc.flink.datagen;

import com.github.scalvetr.poc.flink.customer.model.Customer;
import com.github.scalvetr.poc.flink.datagen.claims.ClaimDataGenerator;
import com.github.scalvetr.poc.flink.datagen.customers.CustomerDataGenerator;
import com.github.scalvetr.poc.flink.datagen.policies.PolicyDataGenerator;
import com.github.scalvetr.poc.flink.datagen.policies.model.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Profile("!test")
@SpringBootApplication
public class DatagenApplication implements CommandLineRunner {
    private static Logger log = LoggerFactory
            .getLogger(DatagenApplication.class);

    private final ClaimDataGenerator claimDataGenerator;
    private final CustomerDataGenerator customerDataGenerator;
    private final PolicyDataGenerator policyDataGenerator;

    public DatagenApplication(ClaimDataGenerator claimDataGenerator, CustomerDataGenerator customerDataGenerator, PolicyDataGenerator policyDataGenerator) {
        this.claimDataGenerator = claimDataGenerator;
        this.customerDataGenerator = customerDataGenerator;
        this.policyDataGenerator = policyDataGenerator;
    }

    public static void main(String[] args) {
        SpringApplication.run(DatagenApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("running DatagenApplication");

        final int cacheSize = 100;
        final int maxPolicies = 3;
        final int sleepTime = 1_000;
        var cache = new ArrayList<Customer>(cacheSize);
        var policiesCache = new HashMap<String, List<Policy>>(cacheSize);

        var random = new Random();
        while (true) {
            Customer customer = null;
            Customer customerRemoved = null;
            Policy policy = null;
            if (cache.size() < cacheSize || random.nextBoolean()) {
                customer = customerDataGenerator.generateCustomer();
                log.info("CUSTOMER: new {}", customer.getCustomerId());
                if (cache.size() >= cacheSize) {
                    customerRemoved = cache.remove(random.nextInt(cache.size()) - 1);
                }
                cache.add(customer);
                Thread.sleep(sleepTime);
            } else {
                customer = cache.get(random.nextInt(cache.size()) - 1);
                log.info("CUSTOMER: existing {}", customer.getCustomerId());
            }
            var policiesList = policiesCache.getOrDefault(customer, new ArrayList<>());
            if (policiesList.size() < maxPolicies || random.nextBoolean()) {
                policy = policyDataGenerator.generatePolicy(customer.getCustomerId());
                log.info("POLICY: new {}", policy.policyId());
                policiesList.add(policy);
                policiesCache.put(customer.getCustomerId(), policiesList);
                Thread.sleep(sleepTime);
            } else {
                policy = policiesList.get(random.nextInt(policiesList.size()) - 1);
                log.info("POLICY: existing {}", policy.policyId());
            }
            var claim = claimDataGenerator.generateClaim(customer.getCustomerId(), policy.policyId());
            log.info("CLAIM: new {}", claim.claimId());
            Thread.sleep(sleepTime);
        }

    }
}
