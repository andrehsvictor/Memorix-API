CREATE TABLE refresh_tokens (
    id         serial       PRIMARY KEY,
    token      varchar(255) NOT NULL,
    user_id    int          NOT NULL,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at timestamp    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
);