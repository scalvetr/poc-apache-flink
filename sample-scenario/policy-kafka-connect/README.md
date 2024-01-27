# Policy - Kafka Connect

Set up a Kafka Connect cluster and configures a MongoDB connector to read the Policy database and publish policies into
a kafka topic.

Start

```shell
skaffold dev -v trace

kubectl -n confluent logs -l app=connect --follow

# see installed connectors list
kubectl -n confluent exec connect-0 -- curl http://connect.confluent:8083/connectors | jq
```