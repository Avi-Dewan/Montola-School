-- V4__add_order_columns.sql
-- Adds order_index column to maintain display sequence for course hierarchy entities

-- CLASSES
ALTER TABLE classes
    ADD COLUMN order_index INT DEFAULT 0;

-- SUBJECTS
ALTER TABLE subjects
    ADD COLUMN order_index INT DEFAULT 0;

-- CHAPTERS
ALTER TABLE chapters
    ADD COLUMN order_index INT DEFAULT 0;

-- TOPICS
ALTER TABLE topics
    ADD COLUMN order_index INT DEFAULT 0;

-- LECTURES
ALTER TABLE lectures
    ADD COLUMN order_index INT DEFAULT 0;

-- INDEXES (optional for sorting performance)
CREATE INDEX idx_classes_order_index ON classes(order_index);
CREATE INDEX idx_subjects_order_index ON subjects(order_index);
CREATE INDEX idx_chapters_order_index ON chapters(order_index);
CREATE INDEX idx_topics_order_index ON topics(order_index);
CREATE INDEX idx_lectures_order_index ON lectures(order_index);