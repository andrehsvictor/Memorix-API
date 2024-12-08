CREATE TABLE IF NOT EXISTS users (
    id             UUID PRIMARY KEY,
    display_name   VARCHAR(255) NOT NULL,
    username       VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password_hash  VARCHAR(255) NOT NULL,
    bio            TEXT,
    avatar_url     VARCHAR(255),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);