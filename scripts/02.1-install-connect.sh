#!/bin/bash

cat <<EOF | kubectl apply -f -
apiVersion: platform.confluent.io/v1beta1
kind: Connect
metadata:
  name: connect
  namespace: confluent
spec:
  replicas: 1
  image:
    application: confluentinc/cp-server-connect:7.5.0
    init: confluentinc/confluent-init-container:2.7.0
  build:
    type: onDemand
    onDemand:
      plugins:
        locationType: confluentHub
        confluentHub:
          - name: kafka-connect-mongodb
            owner: mongodb
            version: "1.9.1"
      storageLimit: 4G
  configOverrides:
    server:
      - group.id=connect-cg.dev.lab.catalog
      - offset.storage.topic=connect-offsets
      - offset.storage.replication.factor=1
      - offset.storage.partitions=5
      - config.storage.topic=connect-configs
      - config.storage.replication.factor=1
      - status.storage.topic=connect-status
      - status.storage.replication.factor=1
      - status.storage.partitions=5
  dependencies:
    kafka:
      bootstrapEndpoint: kafka.confluent.svc.cluster.local:9092
    schemaRegistry:
      url: http://schemaregistry.confluent.svc.cluster.local:8081
EOF
