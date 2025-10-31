package com.evofun.userservice.infrastructure.outbox;

import com.evofun.events.UserRegisteredEvent;
import com.evofun.userservice.infrastructure.kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {
    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);
    private final OutboxService outboxService;
    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        List<OutboxEvent> events = outboxService.findTop50BySentFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                switch (event.getEventType()) {
                    case "UserRegisteredEvent" -> {
                        UserRegisteredEvent userEvent = new ObjectMapper()
                                .readValue(event.getPayload(), UserRegisteredEvent.class);

                        kafkaProducer.sendUserRegistered(userEvent);

                        event.setSent(true);
                        outboxService.save(event);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process OutboxEvent: {}", event.getOutboxId(), e);
            }
        }
    }
}