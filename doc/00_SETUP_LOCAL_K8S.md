# Setup Local Environment

## Prerequisites

* KinD: https://kind.sigs.k8s.io/docs/user/quick-start/
* Kubectl: https://kubernetes.io/docs/tasks/tools/
* Helm: https://helm.sh/

Install on MacOS X

```shell
brew install kind # version 0.20.0
brew install kubernetes-cli # version 1.28.3
brew install helm # version 3.12.1

# Validate kind
kind version

# Validate kubectl
kubectl version
```

## Setup Kubernetes Cluster

* https://istio.io/latest/docs/setup/platform-setup/kind/
* https://kind.sigs.k8s.io/docs/user/local-registry/

set up the local registry
```shell
reg_name='kind-registry'
reg_port='5001'
if [ "$(docker inspect -f '{{.State.Running}}' "${reg_name}" 2>/dev/null || true)" != 'true' ]; then
  docker run \
    -d --restart=always -p "127.0.0.1:${reg_port}:5000" --name "${reg_name}" \
    registry:2
fi

```

```shell
# install ...
cat <<EOF | kind create cluster --image kindest/node:v1.28.0 --name poc-apache-flink --wait 5m --config=-
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
# ... or just start
docker start poc-apache-flink-control-plane

# Ingress NGINX
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
# wait
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s
  
# connect the registry to the cluster network if not already connected
if [ "$(docker inspect -f='{{json .NetworkSettings.Networks.kind}}' "${reg_name}")" = 'null' ]; then
  docker network connect "kind" "${reg_name}"
fi
# Document the local registry
# https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
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