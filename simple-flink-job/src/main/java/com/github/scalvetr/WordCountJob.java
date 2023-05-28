/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class WordCountJob {
    private static final Logger LOG = LoggerFactory.getLogger(WordCountJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("WordCountJob");
        Properties kafkaProperties = new Properties();
        kafkaProperties.load(new FileInputStream(new File("/opt/flink/usrconfig/kafka.properties")));
        String brokers = kafkaProperties.getProperty("bootstrap.servers");
        Properties jobProperties = new Properties();
        jobProperties.load(new FileInputStream(new File("/opt/flink/usrconfig/job.properties")));
        String inputTopic = jobProperties.getProperty("input.topic");
        String outputTopic = jobProperties.getProperty("output.topic");
        String group = jobProperties.getProperty("group_id");

        LOG.info("config -> brokers={}", brokers);
        LOG.info("config -> inputTopic={}", inputTopic);
        LOG.info("config -> outputTopic={}", outputTopic);
        LOG.info("config -> group={}", group);

        final KafkaUtils kafkaUtils = new KafkaUtils(brokers);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // setup source kafka
        final KafkaSource<String> source = kafkaUtils.buildKafkaSource(String.class,
                new SimpleStringSchema(), inputTopic, group);
        final KafkaSink<Tuple2<String, Integer>> sink = kafkaUtils.buildKafkaSink(String.class, Integer.class,
                new SimpleStringSchema(),
                new IntSerializationScheme(), outputTopic);

        final DataStream<String> text = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<Tuple2<String, Integer>> counts =
                // The text lines read from the source are split into words
                // using a user-defined function. The tokenizer, implemented below,
                // will output each words as a (2-tuple) containing (word, 1)
                text.flatMap(new Tokenizer())
                        .name("tokenizer")
                        // keyBy groups tuples based on the "0" field, the word.
                        // Using a keyBy allows performing aggregations and other
                        // stateful transformations over data on a per-key basis.
                        // This is similar to a GROUP BY clause in a SQL query.
                        .keyBy(value -> value.f0)
                        // create windows of windowSize records slided every slideSize records
                        //.countWindow(windowSize, slideSize)
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        // For each key, we perform a simple sum of the "1" field, the count.
                        // If the input data set is bounded, sum will output a final count for
                        // each word. If it is unbounded, it will continuously output updates
                        // each time it sees a new instance of each word in the stream.
                        .sum(1)
                        .name("counter");

        counts.sinkTo(sink);
        // execute program
        env.execute("Word Count");
    }

    public static final class Tokenizer
            implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
