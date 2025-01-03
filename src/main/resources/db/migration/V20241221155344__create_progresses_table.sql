CREATE TABLE IF NOT EXISTS progresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    card_id UUID NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    status VARCHAR(255) NOT NULL,
    ease_factor FLOAT NOT NULL DEFAULT 2.5,
    repetitions INT NOT NULL DEFAULT 0,
    interval INT NOT NULL DEFAULT 1,
    next_repetition TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_studied TIMESTAMP NOT NULL,
    hits INT NOT NULL DEFAULT 0,
    misses INT NOT NULL DEFAULT 0,
    average_time_to_answer INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (user_id, card_id)
);