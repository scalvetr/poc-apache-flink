module datagen

go 1.20

replace (
	datagen/model v0.0.0 => ./model
	datagen/repository v0.0.0 => ./repository
	datagen/util v0.0.0 => ./util
)

require (
	datagen/model v0.0.0
	datagen/repository v0.0.0
	datagen/util v0.0.0
	github.com/google/uuid v1.4.0
	github.com/jaswdr/faker v1.19.1
	go.mongodb.org/mongo-driver v1.13.1
)

require (
	github.com/actgardner/gogen-avro/v10 v10.2.1 // indirect
	github.com/confluentinc/confluent-kafka-go/v2 v2.3.0 // indirect
	github.com/golang/snappy v0.0.4 // indirect
	github.com/heetch/avro v0.4.4 // indirect
	github.com/klauspost/compress v1.13.6 // indirect
	github.com/montanaflynn/stats v0.0.0-20171201202039-1bf9dbcd8cbe // indirect
	github.com/xdg-go/pbkdf2 v1.0.0 // indirect
	github.com/xdg-go/scram v1.1.2 // indirect
	github.com/xdg-go/stringprep v1.0.4 // indirect
	github.com/youmark/pkcs8 v0.0.0-20181117223130-1be2e3e5546d // indirect
	golang.org/x/crypto v0.0.0-20220622213112-05595931fe9d // indirect
	golang.org/x/sync v0.0.0-20220722155255-886fb9371eb4 // indirect
	golang.org/x/text v0.7.0 // indirect
)
