# Simple FLink Job

Deploy a local simple-flink-job

```shell
# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t simple-flink-job:latest

docker tag simple-flink-job:latest localhost:5001/simple-flink-job:latest
docker push localhost:5001/simple-flink-job:latest

# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

# Create FlinkDeployment Yaml and Submit
kubectl apply -f flink-deployment.yaml
# check the logs
kubectl logs -f deploy/simple-flink-job
# check the ui http://simple-flink-job.default.localtest.me/
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