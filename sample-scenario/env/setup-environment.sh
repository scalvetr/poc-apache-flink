#!/bin/bash

. ../../scripts/01-install_k8s.sh
. ../../scripts/02-install_kafka.sh
. ../../scripts/03-install_flink.sh

# mongodb-claimsdb
# https://github.com/bitnami/charts/blob/main/bitnami/mongodb/values.yaml
helm install mongodb oci://registry-1.docker.io/bitnamicharts/mongodb \
--namespace mongodb --create-namespace \
--set auth.rootUser=root \
--set auth.rootPassword=password \
--set auth.usernames="{user,user}" \
--set auth.passwords="{password,password}" \
--set auth.databases="{claimsdb,policiesdb}"

echo "mongodb created"

kubectl apply -f env/mongo-express.yaml

kubectl -n mongodb logs -l app=mongodb-express
kubectl -n mongodb get pods -l app=mongodb-express

# see logs
# kubectl logs -f -l app=mongo -n mongodb
kubectl -n mongodb get pods sample-scenario-env-mongodb-mongodb-0
kubectl -n mongodb logs sample-scenario-env-mongodb-mongodb-0
kubectl -n mongodb describe pod sample-scenario-env-mongodb-mongodb-0