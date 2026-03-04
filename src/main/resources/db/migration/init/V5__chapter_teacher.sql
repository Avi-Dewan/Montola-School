-- db/migration/V4__create_chapter_teacher_table.sql

-- Sequences
CREATE SEQUENCE chapter_teacher_seq START 1 INCREMENT 1;

-- Chapter Teacher Assignment
CREATE TABLE chapter_teacher (
    id              BIGINT      PRIMARY KEY,
    chapter_id      BIGINT      NOT NULL,
    teacher_id      BIGINT      NOT NULL,
    assigned_at     TIMESTAMP   NOT NULL,
    assigned_by     BIGINT      NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT,
    is_deleted      BOOLEAN     DEFAULT FALSE,

    CONSTRAINT uk_chapter_teacher UNIQUE (chapter_id, teacher_id),
    CONSTRAINT fk_chapter_teacher_chapter FOREIGN KEY (chapter_id)
        REFERENCES chapters (id),
    CONSTRAINT fk_chapter_teacher_teacher FOREIGN KEY (teacher_id)
        REFERENCES users (id),
    CONSTRAINT fk_chapter_teacher_assigner FOREIGN KEY (assigned_by)
        REFERENCES users (id)
);

-- Indexes
CREATE INDEX idx_chapter_teacher_chapter ON chapter_teacher(chapter_id);
CREATE INDEX idx_chapter_teacher_teacher ON chapter_teacher(teacher_id);
