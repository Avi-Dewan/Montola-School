-- Create featured_chapters table

CREATE SEQUENCE featured_chapters_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE featured_chapters (
    id          BIGINT  PRIMARY KEY,
    chapter_id  BIGINT  NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    is_deleted  BOOLEAN DEFAULT FALSE,
    version     BIGINT,
    CONSTRAINT  fk_featured_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(id)
);
