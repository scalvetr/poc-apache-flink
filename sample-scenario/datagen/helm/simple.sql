/* https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/datagen/#connector-options */
CREATE TABLE orders_gen
(
    order_number BIGINT,
    price        DECIMAL(32, 2),
    buyer        ROW<first_name STRING, last_name    STRING>,
    order_time   TIMESTAMP(3)
) WITH (
    'connector' = 'datagen'
);

/* https://nightlies.apache.org/flink/flink-docs-master/docs/connectors/table/kafka/#connector-options */

CREATE TABLE orders
WITH (
    'connector' = 'kafka',
    'topic' = 'orders',
    'properties.bootstrap.servers' = 'kafka.confluent:9092',
    'properties.group.id' = 'datagen',
    'format' = 'csv',
    'scan.startup.mode' = 'earliest-offset'
) LIKE orders_gen;

INSERT INTO orders
SELECT *
FROM orders_gen;