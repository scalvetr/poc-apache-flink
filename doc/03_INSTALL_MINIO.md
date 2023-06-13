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

kubectl wait --namespace minio-operator \
  --for=condition=ready pod \
  --selector=app=kafka \
  --timeout=180s


# scale in the operator deployment
kubectl get deployment minio-operator -n minio-operator -o yaml > operator.yaml
yq -i -e '.spec.replicas |= 1' operator.yaml
kubectl apply -f operator.yaml
rm -fR operator.yaml

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
minio-tenant target/tenant-5.0.5.tgz

# setup the ingress
cat <<EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: console
  namespace: minio-tenant
  annotations:
    nginx.ingress.kubernetes.io/backend-protocol: "HTTPS"
  labels:
    app.kubernetes.io/instance: minio-tenant
    app.kubernetes.io/name: myminio-console
spec:
  ingressClassName: nginx
  rules:
  - host: myminio-console.minio-tenant.localtest.me
    http:
      paths:
      - backend:
          service:
            name: myminio-console
            port:
              number: 9443
        pathType: ImplementationSpecific
EOF

echo "See: https://myminio-console.minio-tenant.localtest.me/"
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