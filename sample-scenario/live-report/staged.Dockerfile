FROM maven:3.6-jdk-11 AS builder

# add pom.xml and source code
ADD ./pom.xml pom.xml
ADD ./src src/

RUN mvn clean package

FROM flink:1.17

EXPOSE 6121

# taskmanager rpc
EXPOSE 6122

# jobmanager rpc
EXPOSE 6123

# jobmanager blob server port
EXPOSE 6124

# jobmanager web ui
EXPOSE 8081

# install plugins downloaded via maven
COPY --from=builder target/plugins /opt/flink/plugins

RUN mkdir /opt/flink/usrlib
COPY --from=builder target/sample-scenario-live-report-*.jar /opt/flink/usrlib/flink-job.jar