FROM golang:1.20-alpine3.18 AS build-env

RUN apk update && apk add bash ca-certificates git gcc g++ libc-dev librdkafka-dev pkgconf

# All these steps will be cached
RUN mkdir /app
WORKDIR /app

COPY go.mod .
COPY go.sum .

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

ENV CORE_BANKING_DB_HOST="localhost"
ENV CORE_BANKING_DB_PORT="5432"
ENV CORE_BANKING_DB_USER="user"
ENV CORE_BANKING_DB_PASSWORD="password"
ENV CORE_BANKING_DB_NAME="core-banking"

ENV CONTACT_CENTER_DB_URI="mongodb://localhost:27017"
ENV CONTACT_CENTER_DB_USER="user"
ENV CONTACT_CENTER_DB_PASSWORD="password"
ENV CONTACT_CENTER_DB_NAME="contact-center"

ENTRYPOINT ["/go/bin/app"]