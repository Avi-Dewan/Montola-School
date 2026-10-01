-- Lectures can be served from application object storage instead of YouTube, and a
-- PDF content item can be created before its file has been uploaded.

-- Which provider holds the lecture video: GOOGLE_DRIVE (a YouTube id) or AWS_S3 (an object key).
ALTER TABLE lectures ADD COLUMN storage_provider VARCHAR(30) NOT NULL DEFAULT 'GOOGLE_DRIVE';

-- Object keys are far longer than a YouTube video id.
ALTER TABLE lectures ALTER COLUMN video_id TYPE VARCHAR(500);

-- Keys are also longer than a Drive file id.
ALTER TABLE pdf_google_contents ALTER COLUMN file_id TYPE VARCHAR(500);

-- The file may be attached after the content item is created.
ALTER TABLE pdf_google_contents ALTER COLUMN file_id DROP NOT NULL;
