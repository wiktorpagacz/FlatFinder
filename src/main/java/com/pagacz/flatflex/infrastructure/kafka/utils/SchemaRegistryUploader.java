package com.pagacz.flatflex.infrastructure.kafka.utils;

import generated.avro.FlatData;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;

public class SchemaRegistryUploader {
    private static final String SUBJECT_NAME = "flat-write";
    @Value(value = "${SCHEMA_REGISTRY_URL}")
    private static String schemaRegistryUrl;

    public static void main(String[] args) throws Exception {

        int schemaId;
        try (CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(schemaRegistryUrl, 10)) {

            String schemaString = String.valueOf(FlatData.SCHEMA$);
            Schema schema = new Schema.Parser().parse(schemaString);

            schemaId = client.register(SUBJECT_NAME, schema);
        }
        System.out.println("Zarejestrowano schemat ID: " + schemaId);
    }
}
