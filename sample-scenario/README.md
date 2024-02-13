# Sample Scenario

Sample scenario has the objective to test the different flink apis.

![Sample Scenario](../doc/img/sample_scenario.png)

## Prerequisites

- docker
- kind
- make
- helm
- skaffold

```shell
brew install kind # v0.20.0
brew install make 
brew install helm # v3.13.2
brew install skaffold # v2.9.0
```

Create initial environment
```shell
make setup
```

This command creates the following elements:

* Claims DB: \<\<Mongo DB>>
* Policies DB: \<\<Mongo DB>>

## Build & Run

The projecto consists on the following modules that will be automatically installed by `skaffold`:

* [Datagen](datagen/README.md)

```shell
skaffold dev -v trace

kubectl logs -f deploy/datagen

# check images registered in registry
curl -X GET http://localhost:5001/v2/_catalog
curl -X GET http://localhost:5001/v2/samplescenariodatagen/tags/list
```

URLs:

Mongo Express: http://express.mongodb.localtest.me
Kafka UI: http://kafka-ui.confluent.localtest.me

Claims CDC Job: http://claims-cdc-job.default.localtest.me


## Data gen

Produces data to `Policies DB`, `Claims DB` and `customers` topic.

## Claims CDC Job

Flink job that reads from `Claims DB` and produces to `claims` topic.

## Policy Kafka Connect

Kafka Connect job that reads from `Policies DB` and produces to `policies` topics.


## Live report

FlinkSQL job that runs a query on `policies`, `claims` and `customers` topics and saves the result in `Results DB`.

Flink Job: http://live-report.default.localtest.me/"

Debug

```shell
export POD_NAME="`kubectl get pods -l app=live-report -l component=jobmanager -o custom-columns=":metadata.name" | tail -n1`";
echo ${POD_NAME}

kubectl logs -l app=live-report -l component=jobmanager
```