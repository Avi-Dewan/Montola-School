-- db/migration/content_progress.sql

-- Sequences
CREATE SEQUENCE content_progress_seq START 1 INCREMENT 1;

-- Content Progress
CREATE TABLE content_progress (
    id              BIGINT      PRIMARY KEY,
    user_id         BIGINT      NOT NULL,
    content_item_id BIGINT      NOT NULL,
    is_completed    BOOLEAN     DEFAULT FALSE NOT NULL,
    quiz_score      FLOAT(53),
    last_accessed   TIMESTAMP,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT,
    is_deleted      BOOLEAN     DEFAULT FALSE,

    CONSTRAINT uk_content_progress_user_item UNIQUE (user_id, content_item_id),
    CONSTRAINT fk_content_progress_user FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT fk_content_progress_item FOREIGN KEY (content_item_id)
        REFERENCES content_items (id)
);

-- Indexes
CREATE INDEX idx_progress_user_content ON content_progress(user_id, content_item_id);
