-- Phase 1 : schéma catalogue (+ users préparé pour Phase 2 auth)

CREATE TABLE users (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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
    id                  UUID PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    slug                VARCHAR(255) NOT NULL UNIQUE,
    short_description   TEXT,
    long_description    TEXT,
    publisher           VARCHAR(255),
    developer           VARCHAR(255),
    release_date        DATE,
    base_price          DECIMAL(10, 2) NOT NULL,
    discounted_price    DECIMAL(10, 2),
    discount_end_date   TIMESTAMP,
    platform            VARCHAR(20) NOT NULL,
    pegi_rating         VARCHAR(10),
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    trailer_url         VARCHAR(500),
    cover_image_url     VARCHAR(500),
    min_specs           TEXT,
    recommended_specs   TEXT,
    supported_languages TEXT,
    average_rating      DECIMAL(3, 2) NOT NULL DEFAULT 0,
    review_count        INT NOT NULL DEFAULT 0,
    featured            BOOLEAN NOT NULL DEFAULT FALSE,
    bestseller          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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

CREATE TABLE game_images (
    id         UUID PRIMARY KEY,
    game_id    UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    url        VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    image_type VARCHAR(20) NOT NULL DEFAULT 'SCREENSHOT'
);

CREATE INDEX idx_games_slug ON games(slug);
CREATE INDEX idx_games_status ON games(status);
CREATE INDEX idx_games_platform ON games(platform);
CREATE INDEX idx_games_title ON games(title);
CREATE INDEX idx_games_featured ON games(featured);
CREATE INDEX idx_games_bestseller ON games(bestseller);
