# POC Apache Flink

This POC will install a Flink on Kubernetes using 
[native Kubernetes deployments](https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/resource-providers/native_kubernetes/)
and the [Flink kuberntes operator](https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-main/docs/concepts/overview/), 
and then deploy some simple jobs, developed in java.

## Prerequisites

* [Install local Kubernetes](doc/00_SETUP_LOCAL_K8S.md)
* [Deploy Kafka](doc/01_DEPLOY_KAFKA.md)
* [Install Flink](doc/01_DEPLOY_KAFKA.md)


## Simple Examples

* Simple Stream Job [here](simple-stream-job/README.md)
* Simple Batch Job [here](simple-batch-job/README.md)
* Simple SQL Job [here](simple-sql-job/README.md)
* Simple Statefun App [here](simple-statefun-app/README.md)
