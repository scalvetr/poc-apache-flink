# Simple SQL Job

## Job mode

Deploy a local simple-sql-job

```shell
# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t simple-sql-job:latest

docker tag simple-sql-job:latest localhost:5001/simple-sql-job:latest
docker push localhost:5001/simple-sql-job:latest

# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

# Create FlinkDeployment Yaml and Submit
kubectl apply -f flink-deployment.yaml
# check the logs
kubectl logs -f deploy/simple-sql-job
# check the ui http://simple-sql-job.default.localtest.me/

kubectl get pods\
-l app=simple-sql-job\
-l component=jobmanager

kubectl exec --stdin --tty \
simple-sql-job-f6b57f587-qn7r5 \
-- /bin/bash
```

Produce & consume
```shell
# consume orders
kubectl -n confluent exec kafka-0 -- \
kafka-console-consumer \
--property print.key=true --property key.separator="= " \
--bootstrap-server localhost:9092 \
--topic orders
```

```shell
# delete the job
kubectl delete -f flink-deployment.yaml
```

## Session mode