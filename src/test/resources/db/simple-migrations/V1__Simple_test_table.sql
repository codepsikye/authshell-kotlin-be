-- Simple test migration that works with H2
CREATE TABLE test_table (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO test_table (id, name) VALUES (1, 'Test Record');