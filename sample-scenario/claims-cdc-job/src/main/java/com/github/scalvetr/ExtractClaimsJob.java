/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.scalvetr;

import com.github.scalvetr.poc.flink.claims.model.Claim;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.mongodb.source.MongoSource;
import org.apache.flink.connector.mongodb.source.enumerator.splitter.PartitionStrategy;
import org.apache.flink.connector.mongodb.source.reader.deserializer.MongoDeserializationSchema;
import org.apache.flink.formats.avro.AvroSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Main class for executing SQL scripts.
 */
public class ExtractClaimsJob {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractClaimsJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("ExtractClaimsJob");
        Properties jobProperties = loadProperties(args, 0, "job.properties");
        Properties kafkaSinkProperties = loadProperties(args, 1, "kafka-sink.properties");

        final KafkaUtils kafkaSinkUtils = new KafkaUtils(kafkaSinkProperties);

        String outputTopic = jobProperties.getProperty("output.topic");

        String database = jobProperties.getProperty("mongodb.database");
        String authDatabase = jobProperties.getProperty("mongodb.authDatabase");
        String collection = jobProperties.getProperty("mongodb.collection");
        String username = jobProperties.getProperty("mongodb.username");
        String password = jobProperties.getProperty("mongodb.password");
        String host = jobProperties.getProperty("mongodb.host");
        String port = jobProperties.getProperty("mongodb.port");
        LOG.info("config -> sink.brokers={}", kafkaSinkProperties.getProperty("bootstrap.servers"));
        LOG.info("config -> outputTopic={}", outputTopic);
        LOG.info("config -> mongodb.database={}", database);
        LOG.info("config -> mongodb.authDatabase={}", authDatabase);
        LOG.info("config -> mongodb.collection={}", collection);
        LOG.info("config -> mongodb.host={}", host);
        LOG.info("config -> mongodb.port={}", port);

        MongoSource<Claim> source = MongoSource.<Claim>builder()
                .setUri(String.format("mongodb://%s:%s@%s:%s/?authSource=%s", username, password, host, port, authDatabase))
                .setDatabase(database)
                .setCollection(collection)
                .setFetchSize(2048)
                .setLimit(10000)
                .setNoCursorTimeout(true)
                .setPartitionStrategy(PartitionStrategy.SAMPLE)
                .setPartitionSize(MemorySize.ofMebiBytes(64))
                .setSamplesPerPartition(10)

                .setDeserializationSchema(new MongoDeserializationSchema<Claim>() {
                    @Override
                    public Claim deserialize(BsonDocument document) {
                        return Claim.newBuilder()
                                .setClaimId(document.get("claimId").toString())
                                .setCustomerId(document.get("customer_id").toString())
                                .setPolicyId(document.get("policyId").toString())
                                .setCreationTimestamp(document.get("creationTimestamp").asDouble().longValue()).build();
                    }

                    @Override
                    public TypeInformation<Claim> getProducedType() {
                        return BasicTypeInfo.getInfoFor(Claim.class);
                    }
                })
                .build();
        final KafkaSink<Tuple2<String, Claim>> sink = kafkaSinkUtils.buildKafkaSink(String.class, Claim.class,
                new SimpleStringSchema(),
                AvroSerializationSchema.forSpecific(Claim.class), outputTopic);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        DataStream<Claim> input = env.fromSource(source, WatermarkStrategy.noWatermarks(), "MongoDB-Source");

        // Convert to a key - value tuple
        DataStream<Tuple2<String, Claim>> mapped = input.map(claim -> new Tuple2(claim.getClaimId(), claim));

        mapped.sinkTo(sink);


        //env.enableCheckpointing(5000);
    }

    private static Properties loadProperties(String[] args, int i, String defaultValue) throws Exception {
        Properties props = new Properties();
        if (args.length >= i + 1) {
            props.load(new FileInputStream(new File(args[i])));
        } else {
            props.load(ExtractClaimsJob.class.getClassLoader().getResourceAsStream(defaultValue));
        }
        return props;
    }
}