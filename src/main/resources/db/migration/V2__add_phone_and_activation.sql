-- db/migration/V2__add_phone_and_activation.sql

-- Add phone column to users
ALTER TABLE users
    ADD COLUMN phone VARCHAR(20);

-- Add activation column for clarity
ALTER TABLE users
    ADD COLUMN is_activated BOOLEAN DEFAULT FALSE;

-- Activation tokens table
CREATE TABLE activation_tokens (
                                   id BIGSERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL,
                                   token VARCHAR(255) NOT NULL,
                                   expiry TIMESTAMP NOT NULL,
                                   CONSTRAINT fk_activation_user FOREIGN KEY (user_id)
                                       REFERENCES users (id)
                                       ON DELETE CASCADE
);
