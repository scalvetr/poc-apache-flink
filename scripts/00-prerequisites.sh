#!/bin/bash


brew install kind # version 0.20.0
brew install kubernetes-cli # version 1.28.3
brew install helm # version 3.12.1
brew install minio-mc
brew install yq

# Validate kind
kind version

# Validate kubectl
kubectl version