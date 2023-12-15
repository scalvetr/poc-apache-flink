# Sample Scenario - datagen

## :computer: Build

Maven build

```shell
./mvnw clean package
```

## :running_man: Run

Environment variables

| Property                  | Description                         | Default Value         |
|---------------------------|-------------------------------------|-----------------------|
| CLAIMS_DB_HOST            | MongoDB Host                        | localhost             |
| CLAIMS_DB_PORT            | MongoDB Port                        | 27017                 |
| CLAIMS_DB_DATABASE        | MongoDB Database name               | db                    |
| CLAIMS_DB_AUTH_DATABASE   | MongoDB Auth Database name          | admin                 |
| CLAIMS_DB_USERNAME        | MongoDB Username                    | user                  |
| CLAIMS_DB_PASSWORD        | MongoDB Password                    | password              |
| POLICIES_DB_HOST          | MongoDB Host                        | localhost             |
| POLICIES_DB_PORT          | MongoDB Port                        | 27017                 |
| POLICIES_DB_DATABASE      | MongoDB Database name               | db                    |
| POLICIES_DB_AUTH_DATABASE | MongoDB Auth Database name          | admin                 |
| POLICIES_DB_USERNAME      | MongoDB Username                    | user                  |
| POLICIES_DB_PASSWORD      | MongoDB Password                    | password              |
| KAFKA_BOOTSTRAP_SERVERS   | Bootstrap servers                   | localhost:9092        |
| KAFKA_SCHEMA_REGISTRY     | Schema registry url                 | http://localhost:8081 |
| KAFKA_CUSTOMERS_TOPIC     | Topic where consumers are published | customers             |



Maven run

```shell
docker compose up -d

./mvnw clean spring-boot:run

docker exec -it broker kafka-console-consumer \
--property print.key=true --property key.separator="= " \
--bootstrap-server localhost:9092 \
--topic customers
```
