# POC Apache FLink

This POC will install a Flink on Kubernetes using 
[native Kubernetes deployments](https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/resource-providers/native_kubernetes/)
and the [Flink kuberntes operator](https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-main/docs/concepts/overview/), 
and then deploy a simple job, developed in java.

## Prerequisites

* [Install local Kubernetes](doc/00_SETUP_LOCAL_K8S.md)
* [Deploy Kafka](doc/01_DEPLOY_KAFKA.md)
* [Install Flink](doc/01_DEPLOY_KAFKA.md)

## SCENARIOS

Simple Job [here](simple-flink-job/README.md)

