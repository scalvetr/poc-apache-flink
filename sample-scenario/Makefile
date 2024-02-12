create-local-cluster:
	sh ../scripts/01-create-local-cluster.sh

install-kafka:
	sh ../scripts/02-install-kafka.sh

install-connect:
	sh ../scripts/02.1-install-connect.sh

install-flink:
	sh ../scripts/03-install-flink.sh

install-minio:
	sh ../scripts/04-install-minio.sh

install-mongodb:
	sh ../scripts/05-install-mongodb.sh


setup: create-local-cluster install-kafka install-connect install-flink install-mongodb

cleanup:
	sh ../scripts/00-cleanup-environment.sh
