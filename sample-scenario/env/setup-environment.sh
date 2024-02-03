#!/bin/bash

echo "running 01-install_k8s.sh"
. ../scripts/01-install_k8s.sh
echo "running 01-install_kafka.sh"
. ../scripts/02-install_kafka.sh
echo "running 01-install_flink.sh"
. ../scripts/03-install_flink.sh


echo "installing bitnamicharts/mongodb"
# mongodb-claimsdb
# https://github.com/bitnami/charts/blob/main/bitnami/mongodb/values.yaml
#--set image.tag=6.0.1-debian-11-r11 \
#--set v=false \
#--set replicaSetName=rs0 \
#--set auth.replicaSetKey=sample \
helm install mongodb oci://registry-1.docker.io/bitnamicharts/mongodb --version 14.8.2 \
--namespace mongodb --create-namespace \
--set architecture=replicaset \
--set replicaCount=1 \
--set auth.rootUser=root \
--set auth.rootPassword=password \
--set auth.usernames="{user,user}" \
--set auth.passwords="{password,password}" \
--set auth.databases="{claimsdb,policiesdb}"

echo "mongodb created"

kubectl wait --namespace mongodb \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=mongodb \
  --timeout=180s

echo "installing mongo-express"
kubectl apply -f env/mongo-express.yaml

echo "mongo-express installed"
# delete
# helm uninstall mongodb --namespace mongodb
# kubectl delete -f env/mongo-express.yaml


kubectl -n mongodb logs -l app=mongodb-express
kubectl -n mongodb get pods -l app=mongodb-express

# see logs
# kubectl logs -f -l app=mongo -n mongodb
kubectl -n mongodb get pods -l app.kubernetes.io/component=mongodb
kubectl -n mongodb logs -l app.kubernetes.io/component=mongodb
kubectl -n mongodb describe pod -l app.kubernetes.io/component=mongodb

# kafka connect


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

# kubectl delete -n confluent connect connect