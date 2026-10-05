-- Homepage notices.

CREATE SEQUENCE notices_seq START 1 INCREMENT 1;

CREATE TABLE notices (
    id           BIGINT       PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    message      TEXT,
    type         VARCHAR(30)  NOT NULL DEFAULT 'INFO',
    link         VARCHAR(500),
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    order_index  INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    version      BIGINT,
    is_deleted   BOOLEAN      DEFAULT FALSE
);

CREATE INDEX idx_notice_active_order ON notices(active, order_index);
