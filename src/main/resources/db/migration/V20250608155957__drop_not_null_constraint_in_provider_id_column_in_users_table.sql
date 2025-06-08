ALTER TABLE users
ALTER COLUMN provider_id DROP NOT NULL;
-- This migration drops the NOT NULL constraint from the provider_id column in the users table.