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

# test
kubectl -n mongodb logs -l app=mongodb-express
kubectl -n mongodb get pods -l app=mongodb-express

# see logs
# kubectl logs -f -l app=mongo -n mongodb
kubectl -n mongodb get pods -l app.kubernetes.io/component=mongodb
kubectl -n mongodb logs -l app.kubernetes.io/component=mongodb
kubectl -n mongodb describe pod -l app.kubernetes.io/component=mongodb

# validate environment

kubectl run --namespace mongodb mongodb-client --rm --tty -i --restart='Never' \
--image busybox --command -- \
ping mongodb-arbiter-0.mongodb-arbiter-headless.mongodb.svc.cluster.local

kubectl run --namespace mongodb mongodb-client --rm --tty -i --restart='Never' \
--image busybox --command -- \
ping mongodb-0.mongodb-headless.mongodb.svc.cluster.local

# mongo db
export MONGODB_ROOT_USER="root";
export MONGODB_ROOT_PASSWORD=$(kubectl get secret --namespace mongodb mongodb -o jsonpath="{.data.mongodb-root-password}" | base64 -d)
export MONGODB_PASSWORD=$(kubectl get secret --namespace mongodb mongodb -o jsonpath="{.data.mongodb-passwords}" | base64 -d | awk -F',' '{print $1}')

echo "MONGODB_ROOT_USER=${MONGODB_ROOT_USER}"
echo "MONGODB_ROOT_PASSWORD=${MONGODB_ROOT_PASSWORD}"
echo "MONGODB_PASSWORD=${MONGODB_PASSWORD}"

kubectl run --namespace mongodb mongodb-client --rm --tty -i --restart='Never' \
--env="MONGODB_ROOT_PASSWORD=$MONGODB_ROOT_PASSWORD" \
--image docker.io/bitnami/mongodb:7.0.5-debian-11-r6 \
--command -- \
mongosh admin --host "mongodb-0.mongodb-headless.mongodb.svc.cluster.local:27017" \
--authenticationDatabase admin -u $MONGODB_ROOT_USER -p $MONGODB_ROOT_PASSWORD

> show databases

kubectl -n mongodb logs -l app=mongodb-express

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