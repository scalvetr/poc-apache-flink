## Create java job

```shell
mvn archetype:generate                               \
      -DarchetypeGroupId=org.apache.flink            \
      -DarchetypeArtifactId=flink-quickstart-java    \
      -DarchetypeVersion=1.9.0                       \
      -DgroupId=com.github.scalvetr                  \
      -DartifactId=poc-apache-flink-job              \
      -Dpackage=com.github.scalvetr                  \
      -Dversion=0.0.1-SNAPSHOT
      
```