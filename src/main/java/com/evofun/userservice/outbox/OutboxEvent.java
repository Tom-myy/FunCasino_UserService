package com.evofun.userservice.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OutboxEvent {

    @Id
    @Column (name ="outbox_id", nullable = false, updatable = false)
    private UUID outboxId;

    @Column(name = "aggregate_type", nullable = false, updatable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sent", nullable = false)
    private Boolean sent;

    @PrePersist
    protected void onCreate() {
        outboxId = UUID.randomUUID();
        createdAt = OffsetDateTime.now();//TODO move to DB after learning triggers
        sent = Boolean.FALSE;
    }

    public OutboxEvent(String aggregateType, UUID aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
    }
}
