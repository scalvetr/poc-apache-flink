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

export POD_NAME="`kubectl get pods -l app=simple-sql-job -l component=jobmanager -o custom-columns=":metadata.name"`";

kubectl exec --stdin --tty \           
${POD_NAME} \
-- /bin/bash;

```