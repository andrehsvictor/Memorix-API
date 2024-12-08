CREATE TABLE IF NOT EXISTS decks (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    user_id      UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (name, user_id)
);

CREATE INDEX IF NOT EXISTS idx_decks_user_id ON decks (user_id);
CREATE INDEX IF NOT EXISTS idx_decks_name ON decks (name);