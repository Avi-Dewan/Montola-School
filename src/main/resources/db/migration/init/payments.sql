-- db/migration/payments.sql

-- Sequences
CREATE SEQUENCE payments_seq    START 1 INCREMENT 1;
CREATE SEQUENCE enrollments_seq START 1 INCREMENT 1;

-- Payments
CREATE TABLE payments (
    id              BIGINT       PRIMARY KEY,
    amount          FLOAT(53)    NOT NULL,
    payment_method  VARCHAR(255),
    sender_number   VARCHAR(255) NOT NULL,
    status          VARCHAR(255) NOT NULL,
    transaction_id  VARCHAR(255),
    verified_at     TIMESTAMP,
    chapter_id      BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    verified_by     BIGINT,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT,
    is_deleted      BOOLEAN      DEFAULT FALSE,

    CONSTRAINT uk_payment_transaction_id UNIQUE (transaction_id),
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT fk_payment_chapter FOREIGN KEY (chapter_id)
        REFERENCES chapters (id),
    CONSTRAINT fk_payment_verifier FOREIGN KEY (verified_by)
        REFERENCES users (id)
);

-- Enrollments
CREATE TABLE enrollments (
    id                  BIGINT       PRIMARY KEY,
    enrolled_at         TIMESTAMP    NOT NULL,
    is_completed        BOOLEAN,
    progress_percentage FLOAT(53),
    chapter_id          BIGINT       NOT NULL,
    user_id             BIGINT       NOT NULL,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP,
    version             BIGINT,
    is_deleted          BOOLEAN      DEFAULT FALSE,

    CONSTRAINT uk_enrollment_user_chapter UNIQUE (user_id, chapter_id),
    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT fk_enrollment_chapter FOREIGN KEY (chapter_id)
        REFERENCES chapters (id)
);

-- Indexes
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_user ON payments(user_id);
CREATE INDEX idx_enrollment_user_chapter ON enrollments(user_id, chapter_id);
