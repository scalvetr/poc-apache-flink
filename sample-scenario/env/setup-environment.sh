#!/bin/bash

. ../../scripts/01-install_k8s.sh
. ../../scripts/02-install_kafka.sh
. ../../scripts/03-install_flink.sh


helm repo add community-operator https://mongodb.github.io/helm-charts
helm repo update
helm upgrade --install community-operator \
--namespace mongodb --create-namespace \
--set image.pullPolicy="IfNotPresent" \
community-operator/community-operator

helm upgrade --install sample-scenario-env \
--set mongodb.database=db \
--set mongodb.authenticationDatabase=admin \
--set mongodb.username=user \
--set mongodb.password=password \
--set kafka.topics.customers=customers \
helm

