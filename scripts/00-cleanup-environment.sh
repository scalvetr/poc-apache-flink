#!/bin/bash

kind delete cluster --name poc-apache-flink
docker rm kind-registry