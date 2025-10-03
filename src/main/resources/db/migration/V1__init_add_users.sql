-- db/migration/V1__init-add-users.sql

-- Sequence for user IDs
CREATE SEQUENCE user_seq START 1;

-- Users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_seq'),
    email VARCHAR(255),
    phone VARCHAR(20),
    password_hash VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_activated BOOLEAN DEFAULT FALSE
);

-- User roles table (multiple roles supported)
CREATE TABLE user_roles (
    user_id BIGINT,
    role VARCHAR(50),
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
