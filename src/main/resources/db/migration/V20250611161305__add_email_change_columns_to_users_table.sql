ALTER TABLE users
ADD COLUMN email_change_token VARCHAR(255) NULL,
ADD COLUMN email_change_token_expires_at TIMESTAMP WITH TIME ZONE NULL;