# Simple FLink Job

Deploy a local simple-batch-job

```shell
# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t simple-batch-job:latest

docker tag simple-batch-job:latest localhost:5001/simple-batch-job:latest
docker push localhost:5001/simple-batch-job:latest

# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

# Create FlinkDeployment Yaml and Submit
kubectl apply -f flink-deployment.yaml
# check the logs
kubectl logs -f deploy/simple-batch-job
# check the ui http://simple-batch-job.default.localtest.me/
```

Run the batch
```shell
mc mb testdata/demo-bucket/in
mc mb testdata/demo-bucket/out

mc cp testdata/largefile.txt myminio/demo-bucket/in/largefile.txt --insecure

mc ls myminio/demo-bucket/in --insecure
mc ls myminio/demo-bucket/out --insecure
```

```shell
# delete the job
kubectl delete -f flink-deployment.yaml
```