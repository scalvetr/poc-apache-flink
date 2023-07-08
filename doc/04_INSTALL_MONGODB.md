## Install MongoDb With Helm Operator

See: https://github.com/mongodb/mongodb-kubernetes-operator/blob/master/docs/install-upgrade.md#install-the-operator-using-Helm


Install operator
```shell
helm repo add mongodb https://mongodb.github.io/helm-charts

helm install \
--namespace mongodb \
--create-namespace \
mongodb-operator mongodb/community-operator

```

Deploy a database
```shell
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Namespace
metadata:
  name: mongodb
---
apiVersion: mongodbcommunity.mongodb.com/v1
kind: MongoDBCommunity
metadata:
  name: mongodb
  namespace: mongodb
spec:
  members: 1
  type: ReplicaSet
  version: "6.0.5"
  security:
    authentication:
      modes: ["SCRAM"]
  users:
    - name: user
      db: admin
      passwordSecretRef: # a reference to the secret that will be used to generate the user's password
        name: user-password
      roles:
        - name: clusterAdmin
          db: admin
        - name: userAdminAnyDatabase
          db: admin
      scramCredentialsSecretName: scram
  additionalMongodConfig:
    storage.wiredTiger.engineConfig.journalCompressor: zlib

# the user credentials will be generated from this secret
# once the credentials are generated, this secret is no longer required
---
apiVersion: v1
kind: Secret
metadata:
  name: user-password
  namespace: mongodb
type: Opaque
stringData:
  password: password
EOF

kubectl -n mongodb get events

kubectl wait --namespace mongodb \
  --for=condition=ready pod \
  --selector=app=mongodb-svc \
  --timeout=180s
  
kubectl apply -n mongodb -f doc/mongo-express.yml
```

Test
```shell


```

Uninstall
```shell
helm uninstall mongodb-operator --namespace mongodb-operator
```