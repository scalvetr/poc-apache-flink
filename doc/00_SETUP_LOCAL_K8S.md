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


```shell
make create-local-cluster
```


Uninstall
```shell
kind delete cluster --name poc-apache-flink
docker rm kind-registry
```