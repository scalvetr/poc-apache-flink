package com.github.scalvetr;

import org.apache.flink.api.common.serialization.SerializationSchema;

import java.nio.charset.StandardCharsets;

public class IntSerializationScheme implements SerializationSchema<Integer> {
    @Override
    public byte[] serialize(Integer integer) {
        return ("" + integer).getBytes(StandardCharsets.UTF_8);
    }
}
