package main

import (
	r "datagen/repository"
	u "datagen/util"
	"flag"
	"log"
	"time"
)

func main() {
	keySchemaFile := flag.String("key-schema-file", "customer-key.avsc", "AVRO key schema file")
	valueSchemaFile := flag.String("value-schema-file", "customer-value.avsc", "AVRO value schema file")
	// kafka
	bootstrapServers := u.GetEnv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
	schemaRegistryUrl := u.GetEnv("KAFKA_SCHEMA_REGISTRY_URL", "http://localhost:8081")
	topicName := u.GetEnv("KAFKA_TOPIC_NAME", "event_customer_entity")
	// policies repo
	policiesDbUri := u.GetEnv("POLICIES_DB_URI", "mongodb://localhost:27017")
	policiesDbUser := u.GetEnv("POLICIES_DB_USER", "user")
	policiesDbPassword := u.GetEnv("POLICIES_DB_PASSWORD", "password")
	policiesDbName := u.GetEnv("POLICIES_DB_NAME", "contact-center")
	// claims repo
	claimsDbUri := u.GetEnv("CLAIMS_DB_URI", "mongodb://localhost:27017")
	claimsDbUser := u.GetEnv("CLAIMS_DB_USER", "user")
	claimsDbPassword := u.GetEnv("CLAIMS_DB_PASSWORD", "password")
	claimsDbName := u.GetEnv("CLAIMS_DB_NAME", "contact-center")

	flag.Parse()
	log.Printf("keySchemaFile: %v\n", *keySchemaFile)
	log.Printf("valueSchemaFile: %v\n", *valueSchemaFile)
	log.Printf("bootstrapServers: %v\n", bootstrapServers)
	log.Printf("schemaRegistryUrl: %v\n", schemaRegistryUrl)
	log.Printf("topicName: %v\n", topicName)
	log.Printf("policiesDbUri: %v\n", policiesDbUri)
	log.Printf("policiesDbUser: %v\n", policiesDbUser)
	log.Printf("policiesDbPassword: %v\n", policiesDbPassword)
	log.Printf("policiesDbName: %v\n", policiesDbName)
	log.Printf("claimsDbUri: %v\n", claimsDbUri)
	log.Printf("claimsDbUser: %v\n", claimsDbUser)
	log.Printf("claimsDbPassword: %v\n", claimsDbPassword)
	log.Printf("claimsDbName: %v\n", claimsDbName)

	log.Printf("readSchema(%v)\n", *keySchemaFile)
	keySchema := u.ReadFile(*keySchemaFile)
	log.Printf("readSchema(%v)\n", *valueSchemaFile)
	valueSchema := u.ReadFile(*valueSchemaFile)

	log.Printf("BuildProducer\n")
	kafkaProducer, err := r.BuildProducer(bootstrapServers, schemaRegistryUrl, topicName, keySchema, valueSchema)
	if err != nil {
		panic(err)
	}
	defer kafkaProducer.Close()

	dataGen := BuildDataGenerator(r.MongoDbConfig{
		DbUri:      claimsDbUri,
		DbUser:     claimsDbUser,
		DbPassword: claimsDbPassword,
		DbName:     claimsDbName,
	}, r.MongoDbConfig{
		DbUri:      policiesDbUri,
		DbUser:     policiesDbUser,
		DbPassword: policiesDbPassword,
		DbName:     policiesDbName,
	})
	defer dataGen.Close()

	for {
		// Dummy: ms-customer -> produce to kafka

		// return a customer, it can be either a newly generated one or a previous one
		customer, isNew := dataGen.NextCustomer()
		log.Printf("NextCustomer() = %v, %v\n", customer, isNew)
		if isNew {
			kafkaProducer.ProduceCustomer(customer)
			log.Printf("ProduceCustomer(%v)\n", customer)
		}
		time.Sleep(time.Second * 1)

		// Dummy: core banking -> produce to postgresql
		policy := dataGen.NextPolicy(customer)
		log.Printf("NextPolicy(%v) = %v\n", customer, policy)
		time.Sleep(time.Second * 1)

		// Dummy: Contact Center -> produce to MySql
		claim := dataGen.NextClaim(customer, policy)
		log.Printf("NextClaim(%v) = claim: %v\n", customer.CustomerId, claim)
		time.Sleep(time.Second * 1)
	}
}
