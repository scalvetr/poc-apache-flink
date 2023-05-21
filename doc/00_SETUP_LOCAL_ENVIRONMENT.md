# Setup Local Environment

## Prerequisites

* KinD: https://kind.sigs.k8s.io/docs/user/quick-start/
* Kubectl: https://kubernetes.io/docs/tasks/tools/
* Helm: https://helm.sh/

Install on MacOS X

```shell
brew install kind
brew install kubectl
brew install helm

# Validate kind
kind version

# Validate kubectl
kubectl version
```

## Setup Kubernetes Cluster

https://istio.io/latest/docs/setup/platform-setup/kind/

```shell
# install ...
cat <<EOF | kind create cluster --name poc-apache-flink --wait 5m --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
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
# ... or just start
docker start poc-apache-flink-control-plane

# check connectivity
kubectl cluster-info --context kind-poc-apache-flink

docker port poc-apache-flink-control-plane
# 6443/tcp -> 127.0.0.1:53526
# 30443/tcp -> 0.0.0.0:443
# 30080/tcp -> 0.0.0.0:80

kind get clusters
kubectl config use-context kind-poc-apache-flink
kubectl cluster-info

```

Uninstall
```shell
kind delete cluster --name poc-apache-flink

```