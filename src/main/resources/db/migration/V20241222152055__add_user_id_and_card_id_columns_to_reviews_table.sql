ALTER TABLE reviews ADD COLUMN user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE reviews ADD COLUMN card_id UUID NOT NULL REFERENCES cards (id) ON DELETE CASCADE;