## Install MinIO With Helm Operator

See: https://min.io/docs/minio/kubernetes/upstream/operations/install-deploy-manage/deploy-operator-helm.html

Prerequisites

```shell
brew install minio-mc
brew install yq
```

Install operator
```shell
curl --create-dirs -O --output-dir target -O https://raw.githubusercontent.com/minio/operator/master/helm-releases/operator-5.0.6.tgz

helm install \
--namespace minio-operator \
--create-namespace \
--set console.ingress.enabled=true \
--set console.ingress.ingressClassName=nginx \
--set console.ingress.host=console.minio-operator.localtest.me \
minio-operator target/operator-5.0.6.tgz


# scale in the operator deployment
kubectl get deployment minio-operator -n minio-operator -o yaml > operator.yaml
yq -i -e '.spec.replicas |= 1' operator.yaml
kubectl apply -f operator.yaml
rm -fR operator.yaml


kubectl wait --namespace minio-operator \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/instance=minio-operator-console \
  --timeout=180s


# setup the secret
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
metadata:
  name: console-sa-secret
  namespace: minio-operator
  annotations:
    kubernetes.io/service-account.name: console-sa
type: kubernetes.io/service-account-token
EOF

SA_TOKEN=$(kubectl -n minio-operator  get secret console-sa-secret -o jsonpath="{.data.token}" | base64 --decode)
echo $SA_TOKEN

echo "See: http://console.minio-operator.localtest.me";
```

Deploy a tenant
```shell
curl --create-dirs -O --output-dir target -O https://raw.githubusercontent.com/minio/operator/master/helm-releases/tenant-5.0.6.tgz

helm install \
--namespace minio-tenant1 \
--create-namespace \
--set tenant.name=tenant1 \
--set tenant.configuration.name=tenant1-env-configuration \
--set "tenant.pools[0].servers=1" \
--set secrets.name=tenant1-env-configuration \
--set ingress.api.enabled=true \
--set ingress.api.ingressClassName=nginx \
--set ingress.api.annotations.nginx\\.ingress\\.kubernetes\\.io/backend-protocol=HTTPS \
--set ingress.api.host=api.minio-tenant1.localtest.me \
--set ingress.console.enabled=true \
--set ingress.console.ingressClassName=nginx \
--set ingress.console.annotations.nginx\\.ingress\\.kubernetes\\.io/backend-protocol=HTTPS \
--set ingress.console.host=console.minio-tenant1.localtest.me \
minio-tenant1 target/tenant-5.0.6.tgz

kubectl wait --namespace minio-tenant1 \
  --for=condition=ready pod \
  --selector=v1.min.io/console=tenant1-console \
  --timeout=180s

kubectl -n minio-tenant1 logs -f tenant1-ss-0-0

echo "See: https://console.minio-tenant1.localtest.me/"
echo "username: minio \npassword: minio123"

echo "See: https://api.minio-tenant1.localtest.me/"

mc alias set tenant1 https://api.minio-tenant1.localtest.me minio minio123 --insecure
mc mb tenant1/demo-bucket --insecure
mc mb tenant1/flink --insecure

# test
mc ls tenant1 --insecure


mc admin user svcacct add                                   \
   --access-key "IFRVAFXyBPZFdUOPPE7U"                      \
   --secret-key "JhP251bDsOQHe8ZNM1iEBIhzu2o9pYKTwsokpeCu"  \
   --policy "doc/s3-policy.json"                            \
   tenant1 minio --insecure

```

Uninstall
```shell
helm uninstall minio-tenant1 --namespace minio-tenant1
helm uninstall minio-operator --namespace minio-operator
```