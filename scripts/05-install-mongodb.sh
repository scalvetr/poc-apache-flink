#!/bin/bash


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
