#!/bin/bash


brew install kind
brew install kubectl
brew install helm


reg_name='kind-registry'
reg_port='5001'
if [ "$(docker inspect -f '{{.State.Running}}' "${reg_name}" 2>/dev/null || true)" != 'true' ]; then
  docker run \
    -d --restart=always -p "127.0.0.1:${reg_port}:5000" --name "${reg_name}" \
    registry:2
fi


cat <<EOF | kind create cluster --name poc-apache-flink --wait 5m --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
containerdConfigPatches:
- |-
  [plugins."io.containerd.grpc.v1.cri".registry]
    config_path = ""
  [plugins."io.containerd.grpc.v1.cri".registry.mirrors."localhost:${reg_port}"]
    endpoint = ["http://${reg_name}:5000"]
nodes:
- role: control-plane
  kubeadmConfigPatches:
  - |
    kind: InitConfiguration
    nodeRegistration:
      kubeletExtraArgs:
        node-labels: "ingress-ready=true"
  extraPortMappings:
  - containerPort: 80
    hostPort: 80
    listenAddress: "0.0.0.0" # Optional, defaults to "0.0.0.0"
    protocol: TCP
  - containerPort: 443
    hostPort: 443
    listenAddress: "0.0.0.0" # Optional, defaults to "0.0.0.0"
    protocol: TCP
EOF


kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
# wait
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s


if [ "$(docker inspect -f='{{json .NetworkSettings.Networks.kind}}' "${reg_name}")" = 'null' ]; then
  docker network connect "kind" "${reg_name}"
fi

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: local-registry-hosting
  namespace: kube-public
data:
  localRegistryHosting.v1: |
    host: "localhost:${reg_port}"
    help: "https://kind.sigs.k8s.io/docs/user/local-registry/"
EOF


kubectl create namespace confluent

helm repo add confluentinc https://packages.confluent.io/helm
helm repo update
helm upgrade --install confluent-operator --namespace confluent confluentinc/confluent-for-kubernetes


kubectl apply -n confluent -f - <<EOF
apiVersion: platform.confluent.io/v1beta1
kind: Zookeeper
metadata:
  name: zookeeper
  namespace: confluent
spec:
  replicas: 1
  image:
    application: confluentinc/cp-zookeeper:7.4.0
    init: confluentinc/confluent-init-container:2.6.0
  dataVolumeCapacity: 10Gi
  logVolumeCapacity: 10Gi
  podTemplate:
    resources:
      requests:
        cpu: 100m
        memory: 256Mi
    podSecurityContext:
      fsGroup: 1000
      runAsUser: 1000
      runAsNonRoot: true
EOF


kubectl apply -n confluent -f - <<EOF
apiVersion: platform.confluent.io/v1beta1
kind: Kafka
metadata:
  name: kafka
  namespace: confluent
spec:
  replicas: 1
  image:
    application: confluentinc/cp-server:7.4.0
    init: confluentinc/confluent-init-container:2.6.0
  dataVolumeCapacity: 10Gi
  configOverrides:
    server:
      - "confluent.license.topic.replication.factor=1"
      - "confluent.metrics.reporter.topic.replicas=1"
      - "confluent.tier.metadata.replication.factor=1"
      - "confluent.metadata.topic.replication.factor=1"
      - "confluent.balancer.topic.replication.factor=1"
      - "confluent.security.event.logger.exporter.kafka.topic.replicas=1"
      - "event.logger.exporter.kafka.topic.replicas=1"
      - "offsets.topic.replication.factor=1"
      - "confluent.cluster.link.enable=true"
      - "password.encoder.secret=secret"
  podTemplate:
    resources:
      requests:
        cpu: 200m
        memory: 512Mi
    podSecurityContext:
      fsGroup: 1000
      runAsUser: 1000
      runAsNonRoot: true
  metricReporter:
    enabled: true
EOF



kubectl apply -n confluent -f - <<EOF
apiVersion: platform.confluent.io/v1beta1
kind: SchemaRegistry
metadata:
  name: schemaregistry
  namespace: confluent
spec:
  replicas: 1
  image:
    application: confluentinc/cp-schema-registry:7.4.0
    init: confluentinc/confluent-init-container:2.6.0
  podTemplate:
    resources:
      requests:
        cpu: 100m
        memory: 256Mi
    podSecurityContext:
      fsGroup: 1000
      runAsUser: 1000
      runAsNonRoot: true
EOF

kubectl wait --namespace confluent \
  --for=condition=ready pod \
  --selector=app=kafka \
  --timeout=180s


kubectl create -f https://github.com/jetstack/cert-manager/releases/download/v1.8.2/cert-manager.yaml
helm repo add flink-kubernetes-operator-1.5.0 https://archive.apache.org/dist/flink/flink-kubernetes-operator-1.5.0/
helm install flink-kubernetes-operator flink-kubernetes-operator-1.5.0/flink-kubernetes-operator


brew install minio-mc
brew install yq


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


kubectl port-forward svc/myminio-hl 9000 -n minio-tenant &
mc alias set myminio https://localhost:9000 minio minio123 --insecure
mc mb myminio/demo-bucket --insecure


mc admin user svcacct add                                   \
   --access-key "IFRVAFXyBPZFdUOPPE7U"                      \
   --secret-key "JhP251bDsOQHe8ZNM1iEBIhzu2o9pYKTwsokpeCu"  \
   --policy "doc/s3-policy.json"                            \
   myminio minio --insecure
