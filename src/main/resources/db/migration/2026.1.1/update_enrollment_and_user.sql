-- db/migration/2026.1.1/V2026.1.1__update_enrollment_and_user.sql

-- Add full_name, profile_photo to users
ALTER TABLE users ADD COLUMN full_name VARCHAR(255);
ALTER TABLE users ADD COLUMN profile_photo BYTEA;

-- Add last_completed_content_id to enrollments
ALTER TABLE enrollments ADD COLUMN last_completed_content_id BIGINT;

-- Add foreign key constraint for last_completed_content_id
ALTER TABLE enrollments 
ADD CONSTRAINT fk_enrollment_last_content 
FOREIGN KEY (last_completed_content_id) 
REFERENCES content_items(id);

