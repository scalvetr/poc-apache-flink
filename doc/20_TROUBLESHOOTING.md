# Troubleshooting

## Troubleshoot DNS

```shell
kubectl apply -f - <<EOF
apiVersion: v1
kind: Pod
metadata:
  name: dnsutils
  namespace: default
spec:
  containers:
  - name: dnsutils
    image: registry.k8s.io/e2e-test-images/jessie-dnsutils:1.3
    command:
      - sleep
      - "infinity"
    imagePullPolicy: IfNotPresent
  restartPolicy: Always
EOF

kubectl exec -i -t dnsutils -- nslookup kafka.confluent
```

Test one image

```shell
kubectl get pods -l app=simple-sql-job -l component=jobmanager -o custom-columns=":metadata.name"

export POD_NAME="`kubectl get pods -l app=simple-sql-job -l component=jobmanager -o custom-columns=":metadata.name" | tail -n1`";
#export POD_NAME="`kubectl get pods -l app=datagen -l component=jobmanager -o custom-columns=":metadata.name" | tail -n1`";

kubectl exec --stdin --tty \
${POD_NAME} \
-- /bin/bash;

```

## Docker Registry

```shell
curl -vv http://localhost:5001/v2/_catalog

curl -vv http://localhost:5000/v2/samplescenariodatagen/tags/list

```
## Docker Image

```shell

docker run -it 595a9824acd713b1af3a5025b7e222f2d7a0f6236be01ff0a50bc7d28e77221e find / > pod-content.txt
docker run -it 595a9824acd713b1af3a5025b7e222f2d7a0f6236be01ff0a50bc7d28e77221e env

```