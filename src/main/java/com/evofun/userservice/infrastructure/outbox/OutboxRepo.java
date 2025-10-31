package com.evofun.userservice.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxRepo extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop50BySentFalseOrderByCreatedAtAsc();
}