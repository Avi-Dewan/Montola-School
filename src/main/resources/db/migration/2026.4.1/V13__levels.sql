-- Curriculum levels (JSC / SSC / HSC) and their link to the existing classes table.

CREATE SEQUENCE levels_seq START 1 INCREMENT 1;

CREATE TABLE levels (
    id                  BIGINT       PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    order_index         INTEGER      NOT NULL DEFAULT 0,
    split_into_classes  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP,
    version             BIGINT,
    is_deleted          BOOLEAN      DEFAULT FALSE,

    CONSTRAINT uk_level_name UNIQUE (name)
);

CREATE INDEX idx_levels_order ON levels(order_index);

-- Seed the three levels. JSC is browsed per class (6/7/8); SSC and HSC share
-- subjects across their two years, so they are browsed as whole levels.
INSERT INTO levels (id, name, order_index, split_into_classes, created_at, updated_at, version, is_deleted)
VALUES
    (nextval('levels_seq'), 'JSC', 0, TRUE,  NOW(), NOW(), 0, FALSE),
    (nextval('levels_seq'), 'SSC', 1, FALSE, NOW(), NOW(), 0, FALSE),
    (nextval('levels_seq'), 'HSC', 2, FALSE, NOW(), NOW(), 0, FALSE);

-- Link existing classes to their level based on the first number in the name.
ALTER TABLE classes ADD COLUMN level_id BIGINT;

ALTER TABLE classes
    ADD CONSTRAINT fk_class_level FOREIGN KEY (level_id) REFERENCES levels (id);

CREATE INDEX idx_classes_level ON classes(level_id);

UPDATE classes c
SET level_id = l.id
FROM levels l
WHERE l.name = CASE
    WHEN substring(c.name from '[0-9]+')::int BETWEEN 6 AND 8   THEN 'JSC'
    WHEN substring(c.name from '[0-9]+')::int BETWEEN 9 AND 10  THEN 'SSC'
    WHEN substring(c.name from '[0-9]+')::int BETWEEN 11 AND 12 THEN 'HSC'
END;
