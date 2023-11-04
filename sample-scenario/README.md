# Sample Scenario

Sample scenario has the objective to test the different flink apis.

![Process](../doc/sample_scenario.png)


## Prerequisites

* Install KinD: [00_SETUP_LOCAL_K8S.md](../doc/00_SETUP_LOCAL_K8S.md)
* Deploy Kafka: [01_DEPLOY_KAFKA.md](../doc/01_DEPLOY_KAFKA.md)
* Install Flink: [02_INSTALL_FLINK.md](../doc/02_INSTALL_FLINK.md)

Install skaffold
```shell
brew install skaffold
```

## Build & Run
```shell
skaffold dev -v trace

echo "See: http://datagen.default.localtest.me/"

kubectl logs -f deploy/datagen

# check images registered in registry
curl -X GET http://localhost:5001/v2/_catalog
curl -X GET http://localhost:5001/v2/samplescenariodatagen/tags/list
```

## Data gen

Loads initial data to the Mongo DB databases (Policies DB and Claims DB). 

Debug
```shell
export POD_NAME="`kubectl get pods -l app=datagen -l component=jobmanager -o custom-columns=":metadata.name" | tail -n1`";

kubectl exec --stdin --tty \
${POD_NAME} \
-- /bin/bash;

kubectl exec --stdin --tty \
${POD_NAME} \
-- curl http://schemaregistry.confluent:8081

```