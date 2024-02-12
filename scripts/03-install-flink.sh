#!/bin/bash


kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
kubectl wait --namespace cert-manager \
  --for=condition=ready pod \
  --selector=app=webhook \
  --timeout=180s

# namespace where to deploy tests
kubectl create namespace flinktest

helm repo add flink-kubernetes-operator-1.6.1 https://archive.apache.org/dist/flink/flink-kubernetes-operator-1.6.1/
helm install flink-kubernetes-operator flink-kubernetes-operator-1.6.1/flink-kubernetes-operator \
--namespace flink-operator --create-namespace \
--set watchNamespaces='{flinktest,default}'

#helm uninstall flink-kubernetes-operator --namespace flink-operator

kubectl --namespace flink-operator get all
helm --namespace flink-operator list

kubectl wait --namespace flink-operator \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/name=flink-kubernetes-operator \
  --timeout=240s