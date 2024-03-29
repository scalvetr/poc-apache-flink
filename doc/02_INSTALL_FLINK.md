
# Install Flink

https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-main/docs/try-flink-kubernetes-operator/quick-start/

```shell
kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
kubectl wait --namespace cert-manager \
  --for=condition=ready pod \
  --selector=app=webhook \
  --timeout=180s

# namespace where to deploy tests
kubectl create namespace flinktest

helm repo add flink-kubernetes-operator-1.6.1 https://archive.apache.org/dist/flink/flink-kubernetes-operator-1.6.1/
helm install flink-kubernetes-operator flink-kubernetes-operator-1.6.1/flink-kubernetes-operator \
--namespace flink-operator --create-namespace \
--set watchNamespaces='{flinktest,default}'

#helm uninstall flink-kubernetes-operator --namespace flink-operator

kubectl --namespace flink-operator get all
helm --namespace flink-operator list

kubectl wait --namespace flink-operator \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/name=flink-kubernetes-operator \
  --timeout=180s
```

Deploy a sample job to test the Flink installation

```shell
# Deploy  the job
kubectl create -f https://raw.githubusercontent.com/apache/flink-kubernetes-operator/release-1.6.1/examples/basic.yaml
# check the logs
kubectl logs -f deploy/basic-example
# forward the ui port to the host machine
kubectl port-forward svc/basic-example-rest 8081
# check the UI http://localhost:8081

# delete the job
kubectl delete -f https://raw.githubusercontent.com/apache/flink-kubernetes-operator/release-1.6.1/examples/basic.yaml
```

