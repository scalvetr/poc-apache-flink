#!/bin/bash


kubectl delete -n confluent connect connect
kubectl delete -n confluent schemaregistry schemaregistry
kubectl delete -n confluent zookeeper zookeeper
kubectl delete -n confluent kafka kafka


kubectl delete -n confluent ConfigMap kafka-ui-configmap
helm uninstall -n confluent kafka-ui