# POC Apache FLink

This POC will install a Flink on Kubernetes using 
[native Kubernetes deployments](https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/resource-providers/native_kubernetes/)
and the [Flink kuberntes operator](https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-main/docs/concepts/overview/), 
and then deploy a simple job, developed in java.


To create the Kubernetes cluster follow the following [instructions](doc/00_SETUP_LOCAL_ENVIRONMENT.md)

## Install Flink

https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-main/docs/try-flink-kubernetes-operator/quick-start/

```shell
kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
helm repo add flink-kubernetes-operator-1.5.0 https://archive.apache.org/dist/flink/flink-kubernetes-operator-1.5.0/
helm install flink-kubernetes-operator flink-kubernetes-operator-1.5.0/flink-kubernetes-operator

kubectl get pods
helm list
```

Deploy a sample job to test the Flink installation

```shell
# Deploy  the job
kubectl create -f https://raw.githubusercontent.com/apache/flink-kubernetes-operator/release-1.5/examples/basic.yaml
# check the logs
kubectl logs -f deploy/basic-example
# forward the ui port to the host machine
kubectl port-forward svc/basic-example-rest 8081
# check the UI http://localhost:8081

# delete the job
kubectl delete -f https://raw.githubusercontent.com/apache/flink-kubernetes-operator/release-1.5/examples/basic.yaml
```


Deploy a local poc-apache-flink-job

```shell
cd poc-apache-flink-job

# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t poc-apache-flink-job:latest

docker tag poc-apache-flink-job:latest localhost:5001/poc-apache-flink-job:latest
docker push localhost:5001/poc-apache-flink-job:latest

# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

# Create FlinkDeployment Yaml and Submit
kubectl apply -f flink-deployment.yaml
# check the logs
kubectl logs -f deploy/poc-apache-flink-job
# forward the ui port to the host machine
kubectl port-forward svc/poc-apache-flink-job-rest 8081
# check the UI http://localhost:8081
```

Produce & consume
```shell
# produce words_in
kubectl -n confluent exec --tty --stdin kafka-0 -- \
kafka-console-producer \
--bootstrap-server localhost:9092 \
--topic words_in

# consume wordcount
kubectl -n confluent exec kafka-0 -- \
kafka-console-consumer \
--property print.key=true --property key.separator="= " \
--bootstrap-server localhost:9092 \
--topic wordcount
```

```shell
# delete the job
kubectl delete -f flink-deployment.yaml
```