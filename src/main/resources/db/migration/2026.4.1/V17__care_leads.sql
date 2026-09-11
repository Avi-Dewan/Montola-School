-- Academic Care lead-capture form submissions.
-- NOTE: this table holds real parents' names and phone numbers - treat as personal data.

CREATE SEQUENCE care_leads_seq START 1 INCREMENT 1;

CREATE TABLE care_leads (
    id          BIGINT       PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    phone       VARCHAR(50)  NOT NULL,
    level       VARCHAR(100),
    area        VARCHAR(255),
    message     TEXT,
    status      VARCHAR(30)  NOT NULL DEFAULT 'NEW',
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN      DEFAULT FALSE
);

CREATE INDEX idx_care_lead_status  ON care_leads(status);
CREATE INDEX idx_care_lead_created ON care_leads(created_at);
