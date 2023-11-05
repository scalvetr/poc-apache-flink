module datagen

go 1.20

require (
	github.com/google/uuid v1.4.0
	github.com/jaswdr/faker v1.19.1
	go.mongodb.org/mongo-driver v1.12.1
	datagen/model v0.0.0
	datagen/repository v0.0.0
	datagen/util v0.0.0
)

require (
	github.com/caarlos0/env/v6 v6.9.2 // indirect
	github.com/cenkalti/backoff/v4 v4.1.3 // indirect
	github.com/confluentinc/confluent-kafka-go v1.9.2 // indirect
	github.com/golang/snappy v0.0.4 // indirect
	github.com/hamba/avro v1.7.0 // indirect
	github.com/json-iterator/go v1.1.12 // indirect
	github.com/klauspost/compress v1.13.6 // indirect
	github.com/landoop/schema-registry v0.0.0-20190327143759-50a5701c1891 // indirect
	github.com/modern-go/concurrent v0.0.0-20180306012644-bacd9c7ef1dd // indirect
	github.com/modern-go/reflect2 v1.0.2 // indirect
	github.com/montanaflynn/stats v0.0.0-20171201202039-1bf9dbcd8cbe // indirect
	github.com/mycujoo/go-kafka-avro/v2 v2.0.1 // indirect
	github.com/pkg/errors v0.9.1 // indirect
	github.com/xdg-go/pbkdf2 v1.0.0 // indirect
	github.com/xdg-go/scram v1.1.2 // indirect
	github.com/xdg-go/stringprep v1.0.4 // indirect
	github.com/youmark/pkcs8 v0.0.0-20181117223130-1be2e3e5546d // indirect
	golang.org/x/crypto v0.0.0-20220622213112-05595931fe9d // indirect
	golang.org/x/sync v0.0.0-20220722155255-886fb9371eb4 // indirect
	golang.org/x/text v0.7.0 // indirect
)

replace (
	datagen/model v0.0.0 => ./model
	datagen/repository v0.0.0 => ./repository
	datagen/util v0.0.0 => ./util
)
