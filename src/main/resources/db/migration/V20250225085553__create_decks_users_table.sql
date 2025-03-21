CREATE TABLE IF NOT EXISTS decks_users (
    user_id INTEGER NOT NULL REFERENCES users(id),
    deck_id INTEGER NOT NULL REFERENCES decks(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    access_level VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, deck_id)
);