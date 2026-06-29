DELETE FROM promo_codes WHERE id = 'e6000001-0000-4000-8000-000000000099';

INSERT INTO promo_codes (
    id, code, discount_type, discount_value, min_order_amount, max_usages, usage_count, active
) VALUES (
    'e6000001-0000-4000-8000-000000000099',
    'TESTCRUD',
    'PERCENTAGE',
    5.00,
    0.00,
    10,
    0,
    TRUE
);
