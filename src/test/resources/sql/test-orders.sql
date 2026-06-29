DELETE FROM orders WHERE id IN (
    'd7000001-0000-4000-8000-000000000001',
    'd7000001-0000-4000-8000-000000000002'
);

INSERT INTO orders (
    id, order_number, user_id, status, subtotal, discount_amount, total_amount,
    payment_method, billing_email, created_at, updated_at
) VALUES (
    'd7000001-0000-4000-8000-000000000001',
    'GS-DEMO-001',
    'e5000001-0000-4000-8000-000000000002',
    'PAID',
    48.99,
    0.00,
    48.99,
    'CARD',
    'demo@gamestore.local',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO orders (
    id, order_number, user_id, status, subtotal, discount_amount, total_amount,
    payment_method, billing_email, created_at, updated_at
) VALUES (
    'd7000001-0000-4000-8000-000000000002',
    'GS-ADMIN-001',
    'e5000001-0000-4000-8000-000000000001',
    'PAID',
    48.99,
    0.00,
    48.99,
    'CARD',
    'admin@gamestore.local',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
