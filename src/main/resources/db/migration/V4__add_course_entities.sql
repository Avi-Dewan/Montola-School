-- V4__add_course_entities

-- Classes
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Subjects
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_subject_class FOREIGN KEY (class_id)
      REFERENCES classes (id)
      ON DELETE CASCADE
);

-- Chapters
CREATE TABLE chapters (
    id BIGSERIAL PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_chapter_subject FOREIGN KEY (subject_id)
      REFERENCES subjects (id)
      ON DELETE CASCADE,
    CONSTRAINT fk_chapter_user FOREIGN KEY (created_by)
      REFERENCES users (id)
      ON DELETE CASCADE
);

-- Topics
CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_topic_chapter FOREIGN KEY (chapter_id)
        REFERENCES chapters (id)
        ON DELETE CASCADE
);

-- Lectures
CREATE TABLE lectures (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_lecture_topic FOREIGN KEY (topic_id)
      REFERENCES topics (id)
      ON DELETE CASCADE
);
