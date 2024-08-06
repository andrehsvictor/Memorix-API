CREATE TABLE users (
    id         SERIAL       PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    username   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE decks (
    id          SERIAL       PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    user_id     INTEGER      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE cards (
    id          SERIAL       PRIMARY KEY,
    front       TEXT         NOT NULL,
    back        TEXT         NOT NULL,
    deck_id     INTEGER      NOT NULL,
    easiness    DOUBLE       NOT NULL DEFAULT 2.5,
    interval    INTEGER      NOT NULL DEFAULT 1,
    repetitions INTEGER      NOT NULL DEFAULT 0,
    next_review DATE         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_review DATE         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (deck_id) REFERENCES decks (id)
);