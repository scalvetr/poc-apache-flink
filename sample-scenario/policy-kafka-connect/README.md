# Policy - Kafka Connect

Set up a Kafka Connect cluster and configures a MongoDB connector to read the Policy database and publish policies into
a kafka topic.

Start

```shell
skaffold dev -v trace

kubectl -n confluent logs -l app=connect --follow

# see installed connectors list in connect
kubectl -n confluent exec connect-0 -- curl http://connect.confluent:8083/connectors | jq
kubectl -n confluent exec connect-0 -- curl http://connect.confluent:8083/connectors?expand=status
kubectl -n confluent exec connect-0 -- curl http://connect.confluent:8083/connectors/policy-kafka-connect-mongodb-connector?expand=status
kubectl -n confluent exec connect-0 -- curl http://connect.confluent:8083/connectors?expand=info
kubectl -n confluent exec connect-0 -- curl http://connect.confluent:8083/connectors/policy-kafka-connect-mongodb-connector\?expand\=status \
| jq '.tasks[0].trace' \
| sed 's/\\n/\n/g; s/\\t/\t/g'


kubectl -n confluent logs -l app=schemaregistry --follow

# see installed subjects list in schemaregistry
kubectl -n confluent exec schemaregistry-0 -- curl -X GET -vvv http://schemaregistry.confluent:8081/subjects | jq

kubectl -n confluent exec schemaregistry-0 -- curl -X GET -vvv http://schemaregistry.confluent:8081/schemas/ids/1 | jq 
kubectl -n confluent exec schemaregistry-0 -- curl -X GET -vvv http://schemaregistry.confluent:8081/schemas/ids/2 | jq 

kubectl -n confluent describe connector policy-kafka-connect-mongodb-connector
```