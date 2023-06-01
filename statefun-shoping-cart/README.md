# Shopping Cart Example

```shell

# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t statefun-shoping-cart:latest

docker tag statefun-shoping-cart:latest localhost:5001/statefun-shoping-cart:latest
docker push localhost:5001/statefun-shoping-cart:latest


# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

helm install statefun-shoping-cart k8s
```