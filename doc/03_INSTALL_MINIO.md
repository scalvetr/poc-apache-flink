## Install MinIO With Helm Operator

See: https://min.io/docs/minio/kubernetes/upstream/operations/install-deploy-manage/deploy-operator-helm.html

Prerequisites

```shell
brew install minio-mc
brew install yq
```

Install operator
```shell
curl --create-dirs -O --output-dir target -O https://raw.githubusercontent.com/minio/operator/master/helm-releases/operator-5.0.5.tgz

helm install \
--namespace minio-operator \
--create-namespace \
minio-operator target/operator-5.0.5.tgz


# scale in the operator deployment
kubectl get deployment minio-operator -n minio-operator -o yaml > operator.yaml
yq -i -e '.spec.replicas |= 1' operator.yaml
kubectl apply -f operator.yaml
rm -fR operator.yaml


kubectl wait --namespace minio-operator \
  --for=condition=ready pod \
  --selector=app=kafka \
  --timeout=180s


# setup the ingress
cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: console
  namespace: minio-operator
  labels:
    app.kubernetes.io/instance: minio-operator
    app.kubernetes.io/name: operator
spec:
  ingressClassName: nginx
  rules:
  - host: console.minio-operator.localtest.me
    http:
      paths:
      - backend:
          service:
            name: console
            port:
              number: 9090
        pathType: ImplementationSpecific
EOF

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

echo "See: See: http://console.minio-operator.localtest.me";
```

Deploy a tenant
```shell
curl --create-dirs -O --output-dir target -O https://raw.githubusercontent.com/minio/operator/master/helm-releases/tenant-5.0.5.tgz

helm install \
--namespace minio-tenant \
--create-namespace \
--set ingres.console.enabled=true \
--set ingres.console.ingressClassName=nginx \
--set ingres.console.host=console.minio-tenant.localtest.me \
minio-tenant target/tenant-5.0.5.tgz

echo "See: https://console.minio-tenant.localtest.me/"
echo "username: minio \npassword: minio123"

kubectl port-forward svc/myminio-hl 9000 -n minio-tenant &
mc alias set myminio https://localhost:9000 minio minio123 --insecure
mc mb myminio/demo-bucket --insecure
mc mb myminio/flink --insecure

# test
mc ls myminio --insecure


mc admin user svcacct add                                   \
   --access-key "IFRVAFXyBPZFdUOPPE7U"                      \
   --secret-key "JhP251bDsOQHe8ZNM1iEBIhzu2o9pYKTwsokpeCu"  \
   --policy "doc/s3-policy.json"                            \
   myminio minio --insecure

```