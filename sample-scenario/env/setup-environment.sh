#!/bin/bash

. ../../scripts/01-install_k8s.sh
. ../../scripts/02-install_kafka.sh
. ../../scripts/03-install_flink.sh

# install the mongo community operator
# https://github.com/bitnami/charts/blob/main/bitnami/mongodb/values.yaml
helm install mongodb-claimsdb oci://registry-1.docker.io/bitnamicharts/mongodb \
--namespace mongodb --create-namespace \
--set auth.rootUser=root \
--set auth.rootPassword=password \
--set auth.usernames={user} \
--set auth.passwords={password} \
--set auth.databases={claimsdb}

helm install mongodb-policiesdb oci://registry-1.docker.io/bitnamicharts/mongodb \
--namespace mongodb --create-namespace \
--set auth.rootUser=root \
--set auth.rootPassword=password \
--set auth.usernames={user} \
--set auth.passwords={password} \
--set auth.databases={policiesdb}

# see logs
# kubectl logs -f -l app=mongo -n mongodb
kubectl -n mongodb get pods sample-scenario-env-mongodb-mongodb-0
kubectl -n mongodb logs sample-scenario-env-mongodb-mongodb-0
kubectl -n mongodb describe pod sample-scenario-env-mongodb-mongodb-0