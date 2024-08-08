CREATE TABLE activation_codes (
    id         serial       PRIMARY KEY,
    code       varchar(255) NOT NULL,
    user_id    integer      NOT NULL,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at timestamp    NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (id)
);