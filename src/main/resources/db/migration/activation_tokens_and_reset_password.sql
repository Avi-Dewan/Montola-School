-- db/migration/activation_tokens_and_reset_password.sql

-- Activation tokens table
CREATE TABLE activation_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(255) NOT NULL,
    expiry      TIMESTAMP    NOT NULL,
    CONSTRAINT fk_activation_user FOREIGN KEY (user_id)
       REFERENCES users (id)
       ON DELETE CASCADE
);

CREATE TABLE reset_password_tokens (
    id      BIGSERIAL    PRIMARY KEY,
    user_id BIGINT       NOT NULL,
    token   VARCHAR(255) NOT NULL,
    expiry  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_reset_password_user FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE CASCADE
);
