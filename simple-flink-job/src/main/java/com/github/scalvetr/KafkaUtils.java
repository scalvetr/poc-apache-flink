package com.github.scalvetr;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

public class KafkaUtils {
    final String brokers;

    public KafkaUtils(String brokers) {
        this.brokers = brokers;
    }

    public <K, V> KafkaSink<Tuple2<K, V>> buildKafkaSink(Class<K> keyClass, Class<V> valueClass,
                                                         SerializationSchema<K> serK,
                                                         SerializationSchema<V> serV, String outputTopic) {

        KafkaRecordSerializationSchema<Tuple2<K, V>> ser = KafkaRecordSerializationSchema.<Tuple2<K, V>>builder()
                .setTopic(outputTopic)
                .setKeySerializationSchema(t -> serK.serialize(t.f0))
                .setValueSerializationSchema(t -> serV.serialize(t.f1))
                .build();

        return KafkaSink.<Tuple2<K, V>>builder()
                .setBootstrapServers(brokers)
                .setRecordSerializer(ser)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

    }

    public <T> KafkaSource<T> buildKafkaSource(Class<T> clazz, DeserializationSchema<T> deser,
                                               String inputTopic, String groupId) {

        return KafkaSource.<T>builder()
                .setBootstrapServers(brokers)
                .setTopics(inputTopic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(deser)
                .build();

    }
}
