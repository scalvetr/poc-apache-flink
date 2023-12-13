# Sample Scenario - datagen

## :computer: Build

Maven build

```shell
./mvnw clean package
```

## :running_man: Run

Environment variables

| Property                  | Description                | Default Value |
|---------------------------|----------------------------|---------------|
| CLAIMS_DB_HOST            | MongoDB Host               | localhost     |
| CLAIMS_DB_PORT            | MongoDB Port               | 27017         |
| CLAIMS_DB_DATABASE        | MongoDB Database name      | claimsdb      |
| CLAIMS_DB_AUTH_DATABASE   | MongoDB Auth Database name | admin         |
| CLAIMS_DB_USERNAME        | MongoDB Username           | user          |
| CLAIMS_DB_PASSWORD        | MongoDB Password           | password      |
| POLICIES_DB_HOST          | MongoDB Host               | localhost     |
| POLICIES_DB_PORT          | MongoDB Port               | 27017         |
| POLICIES_DB_DATABASE      | MongoDB Database name      | policiesdb    |
| POLICIES_DB_AUTH_DATABASE | MongoDB Auth Database name | admin         |
| POLICIES_DB_USERNAME      | MongoDB Username           | user          |
| POLICIES_DB_PASSWORD      | MongoDB Password           | password      |


Maven run (embedded mongo)

```shell
./mvnw clean spring-boot:run
```
