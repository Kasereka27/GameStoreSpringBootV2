-- Genres
INSERT INTO genres (id, slug, label) VALUES
    ('a1000001-0000-4000-8000-000000000001', 'action', 'Action'),
    ('a1000001-0000-4000-8000-000000000002', 'rpg', 'RPG'),
    ('a1000001-0000-4000-8000-000000000003', 'fps', 'FPS'),
    ('a1000001-0000-4000-8000-000000000004', 'strategy', 'Stratégie'),
    ('a1000001-0000-4000-8000-000000000005', 'adventure', 'Aventure'),
    ('a1000001-0000-4000-8000-000000000006', 'simulation', 'Simulation'),
    ('a1000001-0000-4000-8000-000000000007', 'sport', 'Sport');

-- Tags
INSERT INTO tags (id, slug, label) VALUES
    ('b2000001-0000-4000-8000-000000000001', 'open-world', 'Open World'),
    ('b2000001-0000-4000-8000-000000000002', 'multiplayer', 'Multijoueur'),
    ('b2000001-0000-4000-8000-000000000003', 'solo', 'Solo'),
    ('b2000001-0000-4000-8000-000000000004', 'coop', 'Coop');

-- Jeux démo
INSERT INTO games (
    id, title, slug, short_description, long_description,
    publisher, developer, release_date, base_price, discounted_price, discount_end_date,
    platform, pegi_rating, status, trailer_url, cover_image_url,
    min_specs, recommended_specs, supported_languages,
    average_rating, review_count, featured, bestseller
) VALUES
(
    'c3000001-0000-4000-8000-000000000001',
    'Cyberpunk 2077 — Ultimate Edition',
    'cyberpunk-2077',
    'RPG futuriste en monde ouvert dans la mégalopole de Night City.',
    'Cyberpunk 2077 est un RPG d''action-aventure en monde ouvert qui se déroule à Night City, une mégalopole obsédée par le pouvoir, le glamour et les modifications corporelles.',
    'CD Projekt', 'CD Projekt RED', '2020-12-10', 59.99, 41.99, '2026-12-31 23:59:59',
    'PC', 'PEGI_18', 'ACTIVE',
    'https://www.youtube.com/embed/LembwKDo1Dk',
    'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&h=500&fit=crop',
    'OS: Win 10 64-bit · CPU: Intel Core i5-3570K · RAM: 8 GB · GPU: GTX 970',
    'OS: Win 10 64-bit · CPU: Intel Core i7-4790 · RAM: 12 GB · GPU: GTX 1060 6GB',
    'Français, Anglais, Allemand, Espagnol',
    4.50, 1284, TRUE, TRUE
),
(
    'c3000001-0000-4000-8000-000000000002',
    'Elden Ring',
    'elden-ring',
    'Action-RPG épique signé FromSoftware en collaboration avec George R.R. Martin.',
    'Leve-toi, Exilé, et sois guidé par la grâce pour brandir la puissance de l''Anneau Elden et devenir un Seigneur Elden dans les Terres Intermédiaires.',
    'Bandai Namco', 'FromSoftware', '2022-02-25', 69.99, 48.99, '2026-12-31 23:59:59',
    'PC', 'PEGI_16', 'ACTIVE',
    'https://www.youtube.com/embed/E3Huy2KrzHQ',
    'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&h=500&fit=crop',
    'OS: Win 10 · CPU: Intel Core i5-8400 · RAM: 12 GB · GPU: GTX 1060 3GB',
    'OS: Win 10/11 · CPU: Intel Core i7-8700K · RAM: 16 GB · GPU: GTX 1070 8GB',
    'Français, Anglais, Japonais',
    4.90, 3421, TRUE, TRUE
),
(
    'c3000001-0000-4000-8000-000000000003',
    'Baldur''s Gate 3',
    'baldurs-gate-3',
    'RPG tactique inspiré de Dungeons & Dragons avec des choix profonds.',
    'Rassemblez votre groupe et retournez aux Royaumes Oubliés dans une histoire d''amitié, de trahison, de sacrifice et de séduction des dieux.',
    'Larian Studios', 'Larian Studios', '2023-08-03', 59.99, 41.99, '2026-12-31 23:59:59',
    'PC', 'PEGI_18', 'ACTIVE',
    'https://www.youtube.com/embed/UuO6HJuSUVg',
    'https://images.unsplash.com/photo-1493710667105-003dca9897c9?w=800&h=500&fit=crop',
    'OS: Win 10 64-bit · CPU: Intel i5-4690 · RAM: 8 GB · GPU: GTX 970',
    'OS: Win 10 64-bit · CPU: Intel i7-8700K · RAM: 16 GB · GPU: RTX 2060',
    'Français, Anglais, Allemand',
    4.80, 2156, TRUE, FALSE
),
(
    'c3000001-0000-4000-8000-000000000004',
    'Starfield Premium',
    'starfield-premium',
    'RPG spatial ambitieux des créateurs de Skyrim et Fallout.',
    'Starfield est le premier nouveau univers en 25 ans de Bethesda Game Studios. Créez n''importe quel personnage et explorez l''espace avec une liberté sans précédent.',
    'Bethesda', 'Bethesda Game Studios', '2023-09-06', 69.99, NULL, NULL,
    'PC', 'PEGI_16', 'ACTIVE',
    'https://www.youtube.com/embed/BQA5lEdHhco',
    'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=800&h=500&fit=crop',
    'OS: Win 10 · CPU: AMD Ryzen 5 2600X · RAM: 16 GB · GPU: RX 5700',
    'OS: Win 11 · CPU: AMD Ryzen 5 3600X · RAM: 16 GB · GPU: RTX 2080',
    'Français, Anglais',
    4.20, 892, FALSE, TRUE
),
(
    'c3000001-0000-4000-8000-000000000005',
    'God of War Ragnarök',
    'god-of-war-ragnarok',
    'Kratos et Atreus affrontent les dieux nordiques dans une épopée grandiose.',
    'Kratos et Atreus doivent voyager à travers chacun des Neuf Royaumes en quête de réponses alors que les forces asgardiennes se préparent à une bataille prophétisée.',
    'Sony Interactive Entertainment', 'Santa Monica Studio', '2022-11-09', 59.99, 47.99, '2026-12-31 23:59:59',
    'PS5', 'PEGI_18', 'ACTIVE',
    'https://www.youtube.com/embed/EE-4GvjKcfs',
    'https://images.unsplash.com/photo-1493710667105-003dca9897c9?w=800&h=500&fit=crop',
    NULL, NULL,
    'Français, Anglais',
    4.80, 1678, FALSE, FALSE
),
(
    'c3000001-0000-4000-8000-000000000006',
    'Hogwarts Legacy',
    'hogwarts-legacy',
    'Aventure en monde ouvert dans l''univers Harry Potter.',
    'Hogwarts Legacy est un RPG immersif se déroulant au XIXe siècle dans l''univers des Harry Potter.',
    'Warner Bros. Games', 'Avalanche Software', '2023-02-10', 59.99, NULL, NULL,
    'PC', 'PEGI_12', 'ACTIVE',
    'https://www.youtube.com/embed/BtyBjOW8sGY',
    'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&h=500&fit=crop',
    'OS: Win 10 · CPU: Intel i5-8400 · RAM: 8 GB · GPU: GTX 1070',
    'OS: Win 10 · CPU: Intel i7-8700 · RAM: 16 GB · GPU: RTX 2080 Ti',
    'Français, Anglais',
    4.60, 1432, FALSE, FALSE
);

-- Genres par jeu
INSERT INTO game_genres (game_id, genre_id) VALUES
    ('c3000001-0000-4000-8000-000000000001', 'a1000001-0000-4000-8000-000000000001'),
    ('c3000001-0000-4000-8000-000000000001', 'a1000001-0000-4000-8000-000000000002'),
    ('c3000001-0000-4000-8000-000000000002', 'a1000001-0000-4000-8000-000000000001'),
    ('c3000001-0000-4000-8000-000000000002', 'a1000001-0000-4000-8000-000000000002'),
    ('c3000001-0000-4000-8000-000000000003', 'a1000001-0000-4000-8000-000000000002'),
    ('c3000001-0000-4000-8000-000000000003', 'a1000001-0000-4000-8000-000000000005'),
    ('c3000001-0000-4000-8000-000000000004', 'a1000001-0000-4000-8000-000000000002'),
    ('c3000001-0000-4000-8000-000000000005', 'a1000001-0000-4000-8000-000000000001'),
    ('c3000001-0000-4000-8000-000000000006', 'a1000001-0000-4000-8000-000000000005');

-- Tags
INSERT INTO game_tags (game_id, tag_id) VALUES
    ('c3000001-0000-4000-8000-000000000001', 'b2000001-0000-4000-8000-000000000001'),
    ('c3000001-0000-4000-8000-000000000001', 'b2000001-0000-4000-8000-000000000003'),
    ('c3000001-0000-4000-8000-000000000002', 'b2000001-0000-4000-8000-000000000001'),
    ('c3000001-0000-4000-8000-000000000002', 'b2000001-0000-4000-8000-000000000003'),
    ('c3000001-0000-4000-8000-000000000003', 'b2000001-0000-4000-8000-000000000003'),
    ('c3000001-0000-4000-8000-000000000003', 'b2000001-0000-4000-8000-000000000004'),
    ('c3000001-0000-4000-8000-000000000004', 'b2000001-0000-4000-8000-000000000001'),
    ('c3000001-0000-4000-8000-000000000006', 'b2000001-0000-4000-8000-000000000001');

-- Captures d'écran
INSERT INTO game_images (id, game_id, url, sort_order, image_type) VALUES
    ('d4000001-0000-4000-8000-000000000001', 'c3000001-0000-4000-8000-000000000002', 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&h=500&fit=crop', 0, 'SCREENSHOT'),
    ('d4000001-0000-4000-8000-000000000002', 'c3000001-0000-4000-8000-000000000002', 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&h=500&fit=crop', 1, 'SCREENSHOT'),
    ('d4000001-0000-4000-8000-000000000003', 'c3000001-0000-4000-8000-000000000002', 'https://images.unsplash.com/photo-1493710667105-003dca9897c9?w=800&h=500&fit=crop', 2, 'SCREENSHOT'),
    ('d4000001-0000-4000-8000-000000000004', 'c3000001-0000-4000-8000-000000000002', 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=800&h=500&fit=crop', 3, 'SCREENSHOT');
