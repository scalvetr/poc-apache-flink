# Shopping Cart Example

```shell

# build the job
./mvnw clean package

# create the image
DOCKER_BUILDKIT=1 docker build . -t statefun-shopping-cart:latest

docker tag statefun-shopping-cart:latest localhost:5001/statefun-shopping-cart:latest
docker push localhost:5001/statefun-shopping-cart:latest


# check the image is deployed
curl -X GET http://localhost:5001/v2/_catalog

helm install statefun-shopping-cart k8s
```