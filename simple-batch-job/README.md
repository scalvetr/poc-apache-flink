# Simple FLink Job

Put the input data in the demo-bucket
```shell
mc mb tenant1/demo-bucket/in/ --insecure
mc mb tenant1/demo-bucket/out/ --insecure

mc cp testdata/largefile.txt tenant1/demo-bucket/in/largefile.txt --insecure

mc ls tenant1/demo-bucket/in --insecure
mc ls tenant1/demo-bucket/out --insecure
```

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

# check the ui
# Flink Job: http://simple-batch-job.default.localtest.me/
# In/Out bucket: https://myminio-console.minio-tenant.localtest.me/browser/demo-bucket
```


```shell
# delete the job
kubectl delete -f flink-deployment.yaml
```