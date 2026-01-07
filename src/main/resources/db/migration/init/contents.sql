-- db/migration/contents.sql
-- ======================================================

-- Sequences
CREATE SEQUENCE lectures_seq                START 1 INCREMENT 2;
CREATE SEQUENCE google_pdf_seq              START 1 INCREMENT 2;
CREATE SEQUENCE quizzes_seq                 START 1 INCREMENT 2;
CREATE SEQUENCE quiz_questions_seq          START 1 INCREMENT 2;
CREATE SEQUENCE quiz_options_seq            START 1 INCREMENT 2;
CREATE SEQUENCE quiz_written_answers_seq    START 1 INCREMENT 2;
CREATE SEQUENCE quiz_fill_blanks_seq        START 1 INCREMENT 2;
CREATE SEQUENCE quiz_table_matching_seq     START 1 INCREMENT 2;


-- ==============================
-- 2️⃣ LECTURES (linked via content_item_id)
-- ==============================

-- Lectures
CREATE TABLE lectures (
      id                BIGINT       PRIMARY KEY,
      content_item_id   BIGINT       NOT NULL,
      video_id          VARCHAR(50),
      content           TEXT,
      created_at        TIMESTAMP,
      updated_at        TIMESTAMP,
      version           BIGINT,
      is_deleted        BOOLEAN      DEFAULT FALSE,

      CONSTRAINT fk_lecture_content_item FOREIGN KEY (content_item_id)
          REFERENCES content_items (id)
          ON DELETE CASCADE
);

-- ==============================
-- 2️⃣ PDF (linked via content_item_id)
-- ==============================

-- google pdf
CREATE TABLE pdf_google_contents (
     id                 BIGINT       PRIMARY KEY DEFAULT nextval('google_pdf_seq'),
     content_item_id    BIGINT       NOT NULL UNIQUE,
     mime_type          VARCHAR(100) NOT NULL,
     size_bytes         BIGINT,
     storage_provider   VARCHAR(30)  NOT NULL,
     file_id            VARCHAR(200) NOT NULL,
     page_count         INT,
     created_at         TIMESTAMP,
     updated_at         TIMESTAMP,
     version            BIGINT,
     is_deleted         BOOLEAN      DEFAULT FALSE,

     CONSTRAINT fk_pdf_content_item
         FOREIGN KEY (content_item_id)
             REFERENCES content_items (id)
             ON DELETE CASCADE
);

-- ==============================
-- 3️⃣ QUIZZES (linked via content_item_id)
-- ==============================

-- QUIZZES
CREATE TABLE quizzes (
     id                 BIGINT       PRIMARY KEY,
     content_item_id    BIGINT       NOT NULL,
     quiz_type          VARCHAR(50)  NOT NULL,       -- 'MCQ' | 'WRITTEN' | 'FILL_BLANK' | 'TABLE_MATCHING'
     instruction        TEXT,
     time_limit         INT,                         -- in minutes
     total_marks        INT,
     pass_percentage DOUBLE PRECISION DEFAULT 60.0,
     created_at         TIMESTAMP,
     updated_at         TIMESTAMP,
     version            BIGINT,
     is_deleted         BOOLEAN      DEFAULT FALSE,

     CONSTRAINT fk_quiz_content_item FOREIGN KEY (content_item_id)
         REFERENCES content_items (id)
         ON DELETE CASCADE
);

-- QUIZ QUESTIONS (common base)
CREATE TABLE quiz_questions (
    id              BIGINT      PRIMARY KEY,
    quiz_id         BIGINT      NOT NULL,
    question_text   TEXT        NOT NULL,
    question_type   VARCHAR(50),                -- optional redundancy
    order_index     INT         NOT NULL DEFAULT 0,
    marks           INT         NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT,
    is_deleted      BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_quiz_question_quiz FOREIGN KEY (quiz_id)
        REFERENCES quizzes (id)
        ON DELETE CASCADE
);

-- MCQ OPTIONS
CREATE TABLE quiz_options (
    id          BIGINT      PRIMARY KEY,
    question_id BIGINT      NOT NULL,
    option_text TEXT        NOT NULL,
    is_correct  BOOLEAN     DEFAULT FALSE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,

    CONSTRAINT fk_option_question FOREIGN KEY (question_id)
      REFERENCES quiz_questions (id)
      ON DELETE CASCADE
);

--  WRITTEN ANSWERS
CREATE TABLE quiz_written_answers (
    id              BIGINT    PRIMARY KEY,
    question_id     BIGINT NOT NULL,
    sample_answer   TEXT,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    version         BIGINT,
    is_deleted      BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_written_question FOREIGN KEY (question_id)
      REFERENCES quiz_questions (id)
      ON DELETE CASCADE
);


--  FILL-IN-THE-BLANKS
CREATE TABLE quiz_fill_blanks (
    id              BIGINT       PRIMARY KEY,
    question_id     BIGINT       NOT NULL,
    blank_position  INT          NOT NULL,
    correct_answer  VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,

    CONSTRAINT fk_fill_blank_question FOREIGN KEY (question_id)
      REFERENCES quiz_questions (id)
      ON DELETE CASCADE
);

--  TABLE MATCHING
CREATE TABLE quiz_table_matching (
    id          BIGINT       PRIMARY KEY,
    question_id BIGINT       NOT NULL,
    left_item   VARCHAR(255) NOT NULL,
    right_item  VARCHAR(255) NOT NULL,
    order_index INT          DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,

    CONSTRAINT fk_table_match_question FOREIGN KEY (question_id)
     REFERENCES quiz_questions (id)
     ON DELETE CASCADE
);

CREATE INDEX idx_lectures_content_item_id ON lectures(content_item_id);
CREATE INDEX idx_quizzes_content_item_id ON quizzes(content_item_id);
CREATE INDEX idx_quiz_questions_quiz_id ON quiz_questions(quiz_id);
CREATE UNIQUE INDEX uq_quiz_questions_order ON quiz_questions (quiz_id, order_index);