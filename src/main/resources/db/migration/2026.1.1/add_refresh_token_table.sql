-- Create sequence for refresh tokens
CREATE SEQUENCE refresh_token_seq START WITH 1 INCREMENT BY 1;

-- Create refresh_tokens table
CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL DEFAULT nextval('refresh_token_seq'),
    user_id BIGINT,
    token VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    version BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uk_refresh_token_token UNIQUE (token),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Add index for faster lookups
CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
