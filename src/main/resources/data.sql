INSERT INTO customers (id, name) VALUES (1, 'Harish');
INSERT INTO customers (id, name) VALUES (2, 'Rajeev');
INSERT INTO customers (id, name) VALUES (3, 'Shankar');

-- Customer 1: June = 115 points
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (1, 120.00, '2026-06-15');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (1, 75.00, '2026-06-28');

-- Customer 1: July = 250 points
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (1, 200.00, '2026-07-10');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (1, 45.00, '2026-07-20');

-- Customer 1: August = 120 points
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (1, 130.00, '2026-08-05');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (1, 60.00, '2026-08-15');

INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (2, 75.00, '2026-06-12');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (2, 120.00, '2026-07-12');
INSERT INTO transactions (customer_id, amount, transaction_date) VALUES (3, 50.00, '2026-08-10');
