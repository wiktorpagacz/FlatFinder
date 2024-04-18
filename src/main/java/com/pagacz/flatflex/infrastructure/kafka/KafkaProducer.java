package com.pagacz.flatflex.infrastructure.kafka;

import generated.avro.FlatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, FlatData> kafkaTemplate;
    @Value("${flat-offer.kafka.topic}")
    private String flatOfferTopic;
    @Autowired
    public KafkaProducer(KafkaTemplate<String, FlatData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(FlatData message) {
        CompletableFuture<SendResult<String, FlatData>> future;
        future = kafkaTemplate.send(flatOfferTopic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(String.format("Sent message=[%s] with offset=[%s]", message, result.getRecordMetadata().offset()));
            } else {
                log.info(String.format("Unable to send message=[%s] due to: %s", message, ex.getMessage()));
            }
        });

    }

}
