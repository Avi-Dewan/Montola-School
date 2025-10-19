-- ======================================================
-- V5__add_content_items.sql
-- Adds unified content_items, quizzes, and question types
-- ======================================================

-- ==============================
-- Sequences
-- ==============================
CREATE SEQUENCE content_items_seq START 1 INCREMENT 1;
CREATE SEQUENCE quizzes_seq START 1 INCREMENT 1;
CREATE SEQUENCE quiz_questions_seq START 1 INCREMENT 1;
CREATE SEQUENCE quiz_options_seq START 1 INCREMENT 1;
CREATE SEQUENCE quiz_written_answers_seq START 1 INCREMENT 1;
CREATE SEQUENCE quiz_fill_blanks_seq START 1 INCREMENT 1;
CREATE SEQUENCE quiz_table_matching_seq START 1 INCREMENT 1;

-- ==============================
-- 1️⃣ CONTENT ITEMS
-- ==============================
CREATE TABLE content_items (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,                -- 'LECTURE' | 'QUIZ'
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    version BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_contentitem_topic FOREIGN KEY (topic_id)
       REFERENCES topics (id)
       ON DELETE CASCADE
);

-- Optional: enforce unique ordering per topic
CREATE UNIQUE INDEX uq_contentitem_topic_order
    ON content_items (topic_id, order_index);


-- ==============================
-- 2️⃣ LECTURES (linked via content_item_id)
-- ==============================
-- Existing lectures table will be refactored to link to content_items
-- but keeping backward compatibility for now.
ALTER TABLE lectures ADD COLUMN content_item_id BIGINT;

ALTER TABLE lectures
    ADD CONSTRAINT fk_lecture_content_item FOREIGN KEY (content_item_id)
        REFERENCES content_items (id)
        ON DELETE CASCADE;

-- Optional: you may drop topic_id later once migration completed
ALTER TABLE lectures DROP COLUMN topic_id;


-- ==============================
-- 3️⃣ QUIZZES (base table)
-- ==============================
CREATE TABLE quizzes (
     id BIGSERIAL PRIMARY KEY,
     content_item_id BIGINT NOT NULL,
     quiz_type VARCHAR(50) NOT NULL,           -- 'MCQ' | 'WRITTEN' | 'FILL_BLANK' | 'TABLE_MATCHING'
     title VARCHAR(200) NOT NULL,
     instruction TEXT,
     time_limit INT,                           -- in seconds/minutes
     total_marks INT,
     created_at TIMESTAMP DEFAULT NOW(),
     updated_at TIMESTAMP,
     version BIGINT,
     is_deleted BOOLEAN DEFAULT FALSE,

     CONSTRAINT fk_quiz_content_item FOREIGN KEY (content_item_id)
         REFERENCES content_items (id)
         ON DELETE CASCADE
);


-- ==============================
-- 4️⃣ QUIZ QUESTIONS (common base)
-- ==============================
CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50),                -- optional redundancy
    order_index INT NOT NULL DEFAULT 0,
    marks INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    version BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_quizquestion_quiz FOREIGN KEY (quiz_id)
        REFERENCES quizzes (id)
        ON DELETE CASCADE
);

-- ==============================
-- 5️⃣ MCQ OPTIONS
-- ==============================
CREATE TABLE quiz_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_option_question FOREIGN KEY (question_id)
      REFERENCES quiz_questions (id)
      ON DELETE CASCADE
);


-- ==============================
-- 6️⃣ WRITTEN ANSWERS
-- ==============================
CREATE TABLE quiz_written_answers (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    sample_answer TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_written_question FOREIGN KEY (question_id)
      REFERENCES quiz_questions (id)
      ON DELETE CASCADE
);


-- ==============================
-- 7️⃣ FILL-IN-THE-BLANKS
-- ==============================
CREATE TABLE quiz_fill_blanks (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    position INT NOT NULL,
    correct_answer VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_fillblank_question FOREIGN KEY (question_id)
      REFERENCES quiz_questions (id)
      ON DELETE CASCADE
);


-- ==============================
-- 8️⃣ TABLE MATCHING
-- ==============================
CREATE TABLE quiz_table_matching (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    left_item VARCHAR(255) NOT NULL,
    right_item VARCHAR(255) NOT NULL,
    order_index INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_tablematch_question FOREIGN KEY (question_id)
     REFERENCES quiz_questions (id)
     ON DELETE CASCADE
);

-- ======================================================
-- ✅ Notes
-- ======================================================
-- 1. content_items.type determines what it links to:
--    - 'LECTURE' → lectures (via content_item_id)
--    - 'QUIZ' → quizzes (via content_item_id)
--
-- 2. quizzes.quiz_type determines which question subtype table applies.
--
-- 3. Future extensions (Assignment, Discussion, etc.)
--    can just add new tables and new type values.
--
-- 4. Eventually you can migrate lectures.topic_id → content_item_id
--    and drop the old link to avoid duplication.
-- ======================================================
