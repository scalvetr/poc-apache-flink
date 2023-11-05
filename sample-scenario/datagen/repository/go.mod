module datagen/repository

go 1.20

require (
	github.com/google/uuid v1.4.0

	go.mongodb.org/mongo-driver v1.12.1

	github.com/confluentinc/confluent-kafka-go v1.9.2
	github.com/mycujoo/go-kafka-avro/v2 v2.0.1
)


replace (
	datagen/model v0.0.0 => ../model
	datagen/util v0.0.0 => ../util
)