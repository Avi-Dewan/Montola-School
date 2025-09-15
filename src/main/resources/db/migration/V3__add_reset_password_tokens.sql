-- db/migration/V3__add_reset_password_tokens.sql

CREATE TABLE reset_password_tokens (
   id BIGSERIAL PRIMARY KEY,
   user_id BIGINT NOT NULL,
   token VARCHAR(255) NOT NULL,
   expiry TIMESTAMP NOT NULL,
   CONSTRAINT fk_reset_password_user FOREIGN KEY (user_id)
       REFERENCES users (id)
       ON DELETE CASCADE
);
