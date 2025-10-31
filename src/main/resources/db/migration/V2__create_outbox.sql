CREATE TABLE outbox (
                        outbox_id      UUID PRIMARY KEY,
                        aggregate_id   UUID        NOT NULL,
                        aggregate_type TEXT        NOT NULL,
                        event_type     TEXT        NOT NULL,
                        payload        JSONB       NOT NULL,
                        created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                        sent           BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_outbox_unsent
    ON user_schema.outbox (sent, created_at);
