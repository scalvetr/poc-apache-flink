FROM apache/flink-statefun:3.2.0

EXPOSE 6121

# taskmanager rpc
EXPOSE 6122

# jobmanager rpc
EXPOSE 6123

# jobmanager blob server port
EXPOSE 6124

# jobmanager web ui
EXPOSE 8081

RUN mkdir /opt/flink/usrlib
ADD target/simple-flink-job-*.jar /opt/flink/usrlib/simple-flink-job.jar