# Deploy Kafka

## Install confluent for kubernetes

```shell
kubectl create namespace confluent

helm repo add confluentinc https://packages.confluent.io/helm
helm repo update
helm upgrade --install confluent-operator --namespace confluent confluentinc/confluent-for-kubernetes
```

## Deploy kafka cluster
https://docs.confluent.io/operator/current/co-api.html
See: https://github.com/confluentinc/confluent-kubernetes-examples/blob/master/quickstart-deploy/confluent-platform-singlenode.yaml

Zookeeper
```shell
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
```


Kafka
```shell
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
```


Schema registry
```shell
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
```


Troubleshooting
```shell
kubectl -n confluent exec --stdin --tty kafka-0 -- /bin/bash

```