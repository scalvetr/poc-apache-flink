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
helm install mongodb oci://registry-1.docker.io/bitnamicharts/mongodb \
--namespace mongodb --create-namespace \
--set architecture=replicaset \
--set replicaSetName=rs0 \
--set replicaCount=1 \
--set replicaSetHostnames=false \
--set auth.replicaSetKey=sample \
--set auth.rootUser=root \
--set auth.rootPassword=password \
--set auth.usernames="{user,user}" \
--set auth.passwords="{password,password}" \
--set auth.databases="{claimsdb,policiesdb}"

echo "mongodb created"

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