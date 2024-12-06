CREATE TABLE IF NOT EXISTS decks (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    user_id UUID NOT NULL,
    cover_url VARCHAR(255),
    accent_color VARCHAR(7),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (name, user_id),
    UNIQUE (slug, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);