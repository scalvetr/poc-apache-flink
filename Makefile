create-local-cluster:
	sh scripts/01-create-local-cluster.sh

install-kafka:
	sh scripts/02-install-kafka.sh

install-flink:
	sh scripts/03-install-flink.sh

install-minio:
	sh scripts/04-install-minio.sh


setup: create-local-cluster install-kafka install-flink install-minio

cleanup:
	sh scripts/00-cleanup-environment.sh