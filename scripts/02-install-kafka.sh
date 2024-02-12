#!/bin/bash


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
    application: confluentinc/cp-zookeeper:7.5.0
    init: confluentinc/confluent-init-container:2.7.0
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

sleep 1
kubectl wait --namespace confluent \
  --for=condition=ready pod \
  --selector=app=zookeeper \
  --timeout=180s


kubectl apply -n confluent -f - <<EOF
apiVersion: platform.confluent.io/v1beta1
kind: Kafka
metadata:
  name: kafka
  namespace: confluent
spec:
  replicas: 1
  image:
    application: confluentinc/cp-server:7.5.0
    init: confluentinc/confluent-init-container:2.7.0
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

sleep 1
kubectl wait --namespace confluent \
  --for=condition=ready pod \
  --selector=app=kafka \
  --timeout=180s


kubectl apply -n confluent -f - <<EOF
apiVersion: platform.confluent.io/v1beta1
kind: SchemaRegistry
metadata:
  name: schemaregistry
  namespace: confluent
spec:
  replicas: 1
  image:
    application: confluentinc/cp-schema-registry:7.5.0
    init: confluentinc/confluent-init-container:2.7.0
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

sleep 1
kubectl wait --namespace confluent \
  --for=condition=ready pod \
  --selector=app=schemaregistry \
  --timeout=180s


helm repo add kafka-ui https://provectus.github.io/kafka-ui-charts

cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: kafka-ui-configmap
  namespace: confluent
data:
  config.yml: |-
    kafka:
      clusters:
        - name: kafka-cluster
          bootstrapServers: kafka.confluent:9092
          schemaRegistry: http://schemaregistry.confluent:8081
          #schemaRegistryAuth:
          #  username: username
          #  password: password
    auth:
      type: disabled
    management:
      health:
        ldap:
          enabled: false
EOF

helm install -n confluent kafka-ui kafka-ui/kafka-ui \
--set yamlApplicationConfigConfigMap.name="kafka-ui-configmap",\
yamlApplicationConfigConfigMap.keyName="config.yml",\
ingress.enabled=true,\
ingress.ingressClassName="nginx",\
ingress.host="kafka-ui.confluent.localtest.me"

sleep 1
kubectl wait --namespace confluent \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/name=kafka-ui \
  --timeout=180s