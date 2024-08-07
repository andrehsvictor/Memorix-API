CREATE TABLE users (
    id         serial       PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    last_name  varchar(255),
    username   varchar(255) NOT NULL,
    email      varchar(255) NOT NULL,
    password   varchar(255) NOT NULL,
    avatar_url varchar(255),
    deleted    boolean      NOT NULL DEFAULT FALSE,
    enabled    boolean      NOT NULL DEFAULT FALSE,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE decks (
    id          serial       PRIMARY KEY,
    name        varchar(255) NOT NULL,
    description text,
    user_id     integer      NOT NULL,
    created_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE cards (
    id          serial       PRIMARY KEY,
    front       text         NOT NULL,
    back        text         NOT NULL,
    deck_id     integer      NOT NULL,
    easiness    float        NOT NULL DEFAULT 2.5,
    interval    integer      NOT NULL DEFAULT 1,
    repetitions integer      NOT NULL DEFAULT 0,
    next_review date         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_review date         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (deck_id) REFERENCES decks (id) ON DELETE CASCADE
);

CREATE TABLE reviews (
    id          serial       PRIMARY KEY,
    card_id     integer      NOT NULL,
    user_id     integer      NOT NULL,
    rating      integer      NOT NULL,
    created_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (card_id) REFERENCES cards (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);