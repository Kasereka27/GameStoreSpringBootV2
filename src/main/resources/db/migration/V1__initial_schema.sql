CREATE TABLE genres (
    id    UUID PRIMARY KEY,
    slug  VARCHAR(50) NOT NULL UNIQUE,
    label VARCHAR(100) NOT NULL
);

CREATE TABLE tags (
    id    UUID PRIMARY KEY,
    slug  VARCHAR(50) NOT NULL UNIQUE,
    label VARCHAR(100) NOT NULL
);

CREATE TABLE games (
    id          UUID PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    platform    VARCHAR(50),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE game_genres (
    game_id  UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    genre_id UUID NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (game_id, genre_id)
);

CREATE TABLE game_tags (
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    tag_id  UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (game_id, tag_id)
);

CREATE INDEX idx_games_slug ON games(slug);
CREATE INDEX idx_games_title ON games(title);
