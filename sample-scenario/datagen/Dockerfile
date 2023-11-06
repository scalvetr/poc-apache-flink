FROM golang:1.20-alpine3.18 AS build-env

RUN apk update && apk add bash ca-certificates git gcc g++ libc-dev librdkafka-dev pkgconf

# All these steps will be cached
RUN mkdir /app
WORKDIR /app

COPY go.mod .
COPY go.sum .
COPY model/go.mod model/
COPY repository/go.mod repository/
COPY util/go.mod util/

# Get dependancies - will also be cached if we won't change mod/sum
RUN go mod download

# COPY the source code as the last step
COPY . .

# https://github.com/confluentinc/confluent-kafka-go/blob/master/kafka/README.md#build-tags
# Build the binary  CGO_ENABLED=0
RUN go build -a -tags musl -o /go/bin/app

FROM alpine:3.18.4

RUN apk add --no-cache libc6-compat bash
COPY --from=build-env /go/bin/app /go/bin/app

# Run
ENV KAFKA_TOPIC_NAME="topic"
ENV KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
ENV KAFKA_SCHEMA_REGISTRY_URL="http://localhost:8081"


ENV POLICIES_DB_URI="mongodb://localhost:27017"
ENV POLICIES_DB_USER="user"
ENV POLICIES_DB_PASSWORD="password"
ENV POLICIES_DB_NAME="policies"

ENV CLAIMS_DB_URI="mongodb://localhost:27017"
ENV CLAIMS_DB_USER="user"
ENV CLAIMS_DB_PASSWORD="password"
ENV CLAIMS_DB_NAME="claims"

ENTRYPOINT ["/go/bin/app"]