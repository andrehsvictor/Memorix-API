ALTER TABLE decks ADD COLUMN published_at TIMESTAMP;

UPDATE decks SET published_at = created_at WHERE visibility = 'PUBLIC';