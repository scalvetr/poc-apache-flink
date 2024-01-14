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
ADD target/plugins /opt/flink/plugins

RUN mkdir /opt/flink/usrlib
ADD target/claims-cdc-job-*.jar /opt/flink/usrlib/flink-job.jar