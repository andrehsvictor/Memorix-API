CREATE TABLE IF NOT EXISTS decks_users_likes (
    deck_id INT NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (deck_id, user_id)
);