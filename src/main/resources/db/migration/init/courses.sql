-- db/migration/courses.sql

-- Sequence for
CREATE SEQUENCE classes_seq         START 1 INCREMENT 1;
CREATE SEQUENCE subjects_seq        START 1 INCREMENT 1;
CREATE SEQUENCE chapters_seq        START 1 INCREMENT 1;
CREATE SEQUENCE topics_seq          START 1 INCREMENT 1;
CREATE SEQUENCE content_items_seq   START 1 INCREMENT 1;

-- Classes
CREATE TABLE classes (
    id          BIGINT       PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN      DEFAULT FALSE
);

-- Subjects
CREATE TABLE subjects (
    id          BIGINT       PRIMARY KEY,
    order_index INT          NOT NULL DEFAULT 0,
    class_id    BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN      DEFAULT FALSE,

    CONSTRAINT fk_subject_class FOREIGN KEY (class_id)
      REFERENCES classes (id)
      ON DELETE CASCADE
);

-- Chapters
CREATE TABLE chapters (
    id          BIGINT       PRIMARY KEY,
    order_index INT          NOT NULL DEFAULT 0,
    subject_id  BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    price       FLOAT(53),
    video_id    VARCHAR(50),
    is_free     BOOLEAN      DEFAULT FALSE,
    created_by  BIGINT       NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN      DEFAULT FALSE,

    CONSTRAINT fk_chapter_subject FOREIGN KEY (subject_id)
      REFERENCES subjects (id)
      ON DELETE CASCADE,
    CONSTRAINT fk_chapter_user FOREIGN KEY (created_by)
      REFERENCES users (id)
      ON DELETE CASCADE
);

-- Topics
CREATE TABLE topics (
    id          BIGINT       PRIMARY KEY,
    order_index INT          NOT NULL DEFAULT 0,
    chapter_id  BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN      DEFAULT FALSE,

    CONSTRAINT fk_topic_chapter FOREIGN KEY (chapter_id)
        REFERENCES chapters (id)
        ON DELETE CASCADE
);

-- Contents
CREATE TABLE content_items (
    id          BIGINT      PRIMARY KEY,
    topic_id    BIGINT      NOT NULL,
    title       VARCHAR(200) NOT NULL,
    type        VARCHAR(50) NOT NULL,
    order_index INT         NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    version     BIGINT,
    is_deleted  BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_content_item_topic FOREIGN KEY (topic_id)
       REFERENCES topics (id)
       ON DELETE CASCADE
);

CREATE INDEX idx_subjects_order_index ON subjects(order_index);
CREATE INDEX idx_chapters_order_index ON chapters(order_index);
CREATE INDEX idx_topics_order_index ON topics(order_index);