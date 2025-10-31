package com.evofun.userservice.infrastructure.outbox;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OutboxService {
    private final OutboxRepo outboxRepo;

    public OutboxService(OutboxRepo outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    public void save(OutboxEvent outbox) {
        outboxRepo.save(outbox);
    }

    public List<OutboxEvent> findTop50BySentFalseOrderByCreatedAtAsc(){
        return outboxRepo.findTop50BySentFalseOrderByCreatedAtAsc();
    }
}