# Shopping Cart Example

```shell

# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t simple-statefun-app:latest

docker tag simple-statefun-app:latest localhost:5001/simple-statefun-app:latest
docker push localhost:5001/simple-statefun-app:latest


# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

helm install simple-statefun-app k8s

helm uninstall simple-statefun-app
```