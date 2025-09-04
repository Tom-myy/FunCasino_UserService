package com.evofun.userservice.kafka;

import com.evofun.events.UserRegisteredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegistered(UserRegisteredEvent event) {
        try {
            kafkaTemplate.send("user-registered", event.userId().toString(), event)
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Kafka event", e);
        }
    }

}