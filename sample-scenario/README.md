# Sample Scenario

Sample scenario has the objective to test the different flink apis.

![Process](../doc/sample_scenario.png)

## Build & Run

```shell
brew install skaffold

skaffold dev

echo "See: http://datagen.default.localtest.me/"
```

## Data gen

Loads initial data to the Mongo DB databases (Policies DB and Claims DB). 

```shell
curl -X GET http://localhost:5001/v2/_catalog
curl -X GET http://localhost:5001/v2/sample-scenario-datagen/tags/list

```