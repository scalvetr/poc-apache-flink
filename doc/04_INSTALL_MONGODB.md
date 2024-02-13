## Install MongoDb With Helm Operator

See: https://github.com/mongodb/mongodb-kubernetes-operator/blob/master/docs/install-upgrade.md#install-the-operator-using-Helm

See installation scrip: [05-install-mongodb.sh](../scripts/05-install-mongodb.sh)

```shell
make install-mongodb
```


Check logs
```shell
# Mongo express
kubectl -n mongodb logs -l app=mongodb-express
kubectl -n mongodb get pods -l app=mongodb-express

# Mongodb
kubectl -n mongodb get pods -l app.kubernetes.io/component=mongodb
kubectl -n mongodb logs -l app.kubernetes.io/component=mongodb
kubectl -n mongodb describe pod -l app.kubernetes.io/component=mongodb
```

Check logs
```shell
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
