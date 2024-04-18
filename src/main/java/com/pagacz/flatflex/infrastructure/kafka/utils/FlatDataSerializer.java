package com.pagacz.flatflex.infrastructure.kafka.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.avro.FlatData;
import org.apache.kafka.common.serialization.Serializer;
import org.jsoup.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FlatDataSerializer implements Serializer<FlatData> {

    Logger log = LoggerFactory.getLogger(FlatDataSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, FlatData data) {
        try {
            if (data == null){
                log.debug("Null received at serializing");
                return null;
            }
            objectMapper.addMixIn(
                    org.apache.avro.specific.SpecificRecord.class, // Interface implemented by all generated Avro-Classes
                    JacksonIgnoreAvroPropertiesMixIn.class);
            log.debug("Serializing...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            log.error(String.format("Error when serializing FlatData to byte[]: %s ", e.getMessage()));
            throw new SerializationException("Error when serializing FlatData to byte[]");
        }
    }

    @Override
    public void close() {
    }
}
