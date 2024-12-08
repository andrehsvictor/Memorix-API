ALTER TABLE decks ADD COLUMN slug VARCHAR(255) NOT NULL;
ALTER TABLE decks ADD UNIQUE (slug, user_id);

CREATE INDEX IF NOT EXISTS idx_decks_slug ON decks(slug);