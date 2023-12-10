package repository

import (
	m "datagen/model"
	u "datagen/util"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
	"github.com/confluentinc/confluent-kafka-go/v2/schemaregistry"
	"github.com/confluentinc/confluent-kafka-go/v2/schemaregistry/serde"
	"github.com/confluentinc/confluent-kafka-go/v2/schemaregistry/serde/avro"
	"log"
)

type KafkaProducer struct {
	producer    *kafka.Producer
	serializer  *avro.SpecificSerializer
	topicName   string
	keySchema   string
	valueSchema string
}

func BuildProducer(bootstrapServers string, schemaRegistryUrl string, topicName string, keySchema string, valueSchema string) (*KafkaProducer, error) {
	log.Printf("repository.BuildProducer\n")

	p, err := kafka.NewProducer(&kafka.ConfigMap{"bootstrap.servers": bootstrapServers})

	if err != nil {
		fmt.Printf("Failed to create producer: %s\n", err)
		return nil, err
	}

	fmt.Printf("Created Producer %v\n", p)

	client, err := schemaregistry.NewClient(schemaregistry.NewConfig(schemaRegistryUrl))

	if err != nil {
		fmt.Printf("Failed to create schema registry client: %s\n", err)
		return nil, err
	}

	ser, err := avro.NewSpecificSerializer(client, serde.ValueSerde, avro.NewSerializerConfig())

	if err != nil {
		fmt.Printf("Failed to create serializer: %s\n", err)
		return nil, err
	}

	return &KafkaProducer{
		producer:    p,
		serializer:  ser,
		topicName:   topicName,
		keySchema:   keySchema,
		valueSchema: valueSchema}, nil

}

func (p KafkaProducer) ProduceCustomer(customer m.CustomerStruct) error {
	key := customer.CustomerId
	value := u.ToMap(customer)
	log.Printf("avroProducer.Produce(%v, %v)\n", key, value)

	// Optional delivery channel, if not specified the Producer object's
	// .Events channel is used.
	deliveryChan := make(chan kafka.Event)

	payload, err := p.serializer.Serialize(p.topicName, &value)
	if err != nil {
		fmt.Printf("Failed to serialize payload: %s\n", err)
		return err
	}

	err = p.producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &p.topicName, Partition: kafka.PartitionAny},
		Value:          payload,
		Headers:        []kafka.Header{{Key: "myTestHeader", Value: []byte("header values are binary")}},
	}, deliveryChan)
	if err != nil {
		fmt.Printf("Produce failed: %v\n", err)
		return err
	}

	e := <-deliveryChan
	m := e.(*kafka.Message)

	if m.TopicPartition.Error != nil {
		fmt.Printf("Delivery failed: %v\n", m.TopicPartition.Error)
	} else {
		fmt.Printf("Delivered message to topic %s [%d] at offset %v\n",
			*m.TopicPartition.Topic, m.TopicPartition.Partition, m.TopicPartition.Offset)
	}

	close(deliveryChan)
	log.Printf("item sent: %v\n", customer)
	return nil
}

func (p KafkaProducer) Close() {
	p.producer.Close()
	fmt.Println("Kafka producer closed.")
}
