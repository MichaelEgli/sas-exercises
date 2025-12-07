-- ===================================
-- CLEANUP
-- ===================================
TRUNCATE TABLE ORDER_ITEM CASCADE;
TRUNCATE TABLE BOOK_ORDER CASCADE;
TRUNCATE TABLE PAYMENT CASCADE;
TRUNCATE TABLE CUSTOMER CASCADE;

-- ===================================
-- RESET SEQUENCES
-- ===================================
ALTER SEQUENCE customer_seq RESTART WITH 10000;
ALTER SEQUENCE payment_seq RESTART WITH 1000;
ALTER SEQUENCE book_order_seq RESTART WITH 100000;
ALTER SEQUENCE order_item_seq RESTART WITH 1000;

-- ===================================
-- CUSTOMERS
-- ===================================
INSERT INTO CUSTOMER (ID, FIRST_NAME, LAST_NAME, EMAIL,
                      ADDRESS_STREET, ADDRESS_CITY, ADDRESS_POSTAL_CODE, ADDRESS_STATE_PROVINCE, ADDRESS_COUNTRY,
                      CREDIT_CARD_NUMBER, CREDIT_CARD_TYPE, CREDIT_CARD_EXPIRATION_MONTH, CREDIT_CARD_EXPIRATION_YEAR)
VALUES (nextval('customer_seq'), 'Anna', 'Schmidt', 'anna.schmidt@example.com',
        'Bahnhofstrasse 1', 'Zurich', '8001', 'ZH', 'Switzerland',
        '4111 1111 1111 1111', 'VISA', 12, 2026),
       (nextval('customer_seq'), 'Max', 'Muller', 'max.muller@example.com',
        'Hauptstrasse 10', 'Bern', '3000', 'BE', 'Switzerland',
        '5500 0000 0000 0004', 'MASTERCARD', 6, 2027),
       (nextval('customer_seq'), 'Susi', 'Mueller', 'susi.mueller@example.com',
        'Seestrasse 25', 'Lucerne', '6000', 'LU', 'Switzerland',
        '3782 822463 10005', 'VISA', 9, 2028);

-- ===================================
-- PAYMENTS (10000, 10050, 10100)
-- ===================================
INSERT INTO PAYMENT (ID, PAYMENT_DATE, AMOUNT, CREDIT_CARD_NUMBER, TRANSACTION_ID)
VALUES (nextval('payment_seq'), '2024-01-15 10:30:00', 150.00, '4111 1111 1111 1111', 'TXN-100'),
       (nextval('payment_seq'), '2024-02-20 14:45:00', 85.00, '5500 0000 0000 0004', 'TXN-101'),
       (nextval('payment_seq'), '2024-03-05 09:15:00', 120.00, '3782 822463 10005', 'TXN-102'),
       (nextval('payment_seq'), '2024-04-10 11:00:00', 200.00, '4111 1111 1111 1111', 'TXN-103');

-- ===================================
-- ORDERS (Customer IDs: 10000, 10050, 10100 | Payment IDs: 1000, 1050, 1100, 1150)
-- ===================================
INSERT INTO BOOK_ORDER (ID, ORDER_DATE, AMOUNT, STATUS, CUSTOMER_ID, PAYMENT_ID,
                        ADDRESS_STREET, ADDRESS_CITY, ADDRESS_POSTAL_CODE, ADDRESS_STATE_PROVINCE, ADDRESS_COUNTRY)
VALUES (nextval('book_order_seq'), '2024-01-15 10:30:00', 150.00, 'ACCEPTED', 10000, 1000,
        'Bahnhofstrasse 1', 'Zurich', '8001', 'ZH', 'Switzerland'),
       (nextval('book_order_seq'), '2024-02-20 14:45:00', 85.00, 'SHIPPED', 10050, 1050,
        'Hauptstrasse 10', 'Bern', '3000', 'BE', 'Switzerland'),
       (nextval('book_order_seq'), '2024-03-05 09:15:00', 120.00, 'SHIPPED', 10100, 1100,
        'Seestrasse 25', 'Lucerne', '6000', 'LU', 'Switzerland'),
       (nextval('book_order_seq'), '2024-04-10 11:00:00', 200.00, 'PROCESSING', 10000, 1150,
        'Bahnhofstrasse 1', 'Zurich', '8001', 'ZH', 'Switzerland');

-- ===================================
-- ORDER ITEMS (Order IDs: 100000, 100050, 100100, 100150)
-- ===================================
INSERT INTO ORDER_ITEM (ID, QUANTITY, ORDER_ID, BOOK_ISBN, BOOK_TITLE, BOOK_AUTHORS, BOOK_PUBLISHER, BOOK_PRICE)
VALUES (nextval('order_item_seq'), 2, 100000, '978-3-16-148410-0', 'Spring Boot in Action', 'Craig Walls', 'Manning',
        45.00),
       (nextval('order_item_seq'), 1, 100000, '978-1-491-91866-2', 'Java Performance', 'Scott Oaks', 'O''Reilly',
        60.00),
       (nextval('order_item_seq'), 1, 100050, '978-0-13-468599-1', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley',
        85.00),
       (nextval('order_item_seq'), 3, 100100, '978-0-596-52068-7', 'Head First Design Patterns',
        'Eric Freeman, Bert Bates, Kathy Sierra, Elisabeth Robson', 'O''Reilly', 40.00),
       (nextval('order_item_seq'), 4, 100150, '978-1-491-94728-6', 'Learning Spring Boot 2.0', 'Greg L. Turnquist',
        'O''Reilly', 50.00);
