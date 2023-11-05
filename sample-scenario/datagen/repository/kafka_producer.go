package repository

import (
	m "datagen/model"
	u "datagen/util"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"github.com/mycujoo/go-kafka-avro/v2"
	"log"
)

type KafkaProducer struct {
	producer *kafkaavro.Producer
}

func BuildProducer(bootstrapServers string, schemaRegistryUrl string, topicName string, keySchema string, valueSchema string) (*KafkaProducer, error) {
	log.Printf("kafkaavro.NewCachedSchemaRegistryClient\n")
	srClient, err := kafkaavro.NewCachedSchemaRegistryClient(schemaRegistryUrl)
	if err != nil {
		return nil, err
	}

	log.Printf("kafkaavro.NewProducer\n")
	avroProducer, err := kafkaavro.NewProducer(
		topicName, keySchema, valueSchema,
		kafkaavro.WithKafkaConfig(&kafka.ConfigMap{
			"bootstrap.servers": bootstrapServers,
		}),
		kafkaavro.WithSchemaRegistryClient(srClient),
	)
	if err != nil {
		return nil, err
	}
	//defer avroProducer.Close()

	return &KafkaProducer{producer: avroProducer}, nil

}

func (p KafkaProducer) ProduceCustomer(customer m.CustomerStruct) {
	key := customer.CustomerId
	value := u.ToMap(customer)
	log.Printf("avroProducer.Produce(%v, %v)\n", key, value)
	err := p.producer.Produce(key, value, nil)
	if err != nil {
		panic(err)
	}
	log.Printf("item sent: %v\n", customer)
}

func (p KafkaProducer) Close() {
	p.producer.Close()
	fmt.Println("Kafka producer closed.")
}
