-- Phase 2 : tokens de réinitialisation + comptes démo

CREATE TABLE password_reset_tokens (
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);

-- Admin : admin@gamestore.local / Admin123!
-- Client : demo@gamestore.local / Demo1234!
INSERT INTO users (id, email, password_hash, first_name, last_name, role, enabled, email_verified)
VALUES
    ('e5000001-0000-4000-8000-000000000001',
     'admin@gamestore.local',
     '$2a$10$SswoDOh.t353J.jEysNqDe7xZp38cQegvp7RQU4igDT1GxmcF3iB.',
     'Sophie', 'Admin', 'ROLE_ADMIN', TRUE, TRUE),
    ('e5000001-0000-4000-8000-000000000002',
     'demo@gamestore.local',
     '$2a$10$7e.ABImtaqW46xRIPaHwyujE1EObYlGYZ0aYr3Iiblu0TeRrLPy5y',
     'Alex', 'Martin', 'ROLE_USER', TRUE, TRUE);
