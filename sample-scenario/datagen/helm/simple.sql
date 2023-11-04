/* https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/connectors/table/datagen/#connector-options */
CREATE TABLE orders_gen
(
    order_number BIGINT,
    price        DECIMAL(32, 2),
    buyer        ROW<first_name STRING, last_name    STRING>,
    order_time   TIMESTAMP(3)
) WITH (
    'connector' = 'datagen'
);

/* https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/connectors/table/kafka/#connector-options */
/* https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/connectors/table/formats/avro-confluent/#format-options */
CREATE TABLE orders
WITH (
    'connector' = 'kafka',
    'topic' = 'orders',
    'properties.bootstrap.servers' = 'kafka.confluent:9092',

    -- UTF-8 string as Kafka keys, using the 'the_kafka_key' table column
    'key.format' = 'raw',
    'key.fields' = 'order_number',

    'value.format' = 'avro-confluent',
    'value.avro-confluent.url' = 'http://schemaregistry.confluent:8081'

) LIKE orders_gen;

INSERT INTO orders
SELECT *
FROM orders_gen;