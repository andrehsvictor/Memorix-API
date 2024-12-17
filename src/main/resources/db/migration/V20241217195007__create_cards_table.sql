CREATE TABLE IF NOT EXISTS cards (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question             TEXT NOT NULL,
    answer               TEXT,
    boolean_answer       BOOLEAN,
    type                 VARCHAR(255) NOT NULL,
    options              TEXT,
    correct_option_index INT,
    deck_id              UUID NOT NULL REFERENCES decks (id) ON DELETE CASCADE,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
