-- Phase 3 : avis et notes sur les jeux

CREATE TABLE game_reviews (
    id                UUID PRIMARY KEY,
    game_id           UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    user_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating            INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    content           TEXT NOT NULL,
    verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count     INT NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (game_id, user_id)
);

CREATE INDEX idx_game_reviews_game_id ON game_reviews(game_id);
CREATE INDEX idx_game_reviews_user_id ON game_reviews(user_id);

-- Avis démo sur Elden Ring
INSERT INTO game_reviews (id, game_id, user_id, rating, content, verified_purchase, helpful_count, created_at)
VALUES
    ('f6000001-0000-4000-8000-000000000001',
     'c3000001-0000-4000-8000-000000000002',
     'e5000001-0000-4000-8000-000000000002',
     5,
     'Chef-d''œuvre absolu. L''exploration est addictive et chaque zone réserve des surprises. Le combat est exigeant mais gratifiant.',
     TRUE, 42, '2026-05-12 14:30:00'),
    ('f6000001-0000-4000-8000-000000000002',
     'c3000001-0000-4000-8000-000000000002',
     'e5000001-0000-4000-8000-000000000001',
     5,
     'Clé reçue en quelques secondes, activation Steam sans problème. Le jeu est immense — plus de 100 heures de contenu.',
     TRUE, 28, '2026-04-28 09:15:00');
