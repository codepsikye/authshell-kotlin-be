-- This migration is designed to fail for testing purposes
-- It contains intentional syntax errors to trigger migration failure

-- Invalid SQL syntax to cause failure
CREATE TABLE invalid_table (
    id INVALID_TYPE_THAT_DOES_NOT_EXIST,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT invalid_constraint FOREIGN KEY (nonexistent_column) REFERENCES nonexistent_table(id)
);

-- Another invalid statement
INSERT INTO nonexistent_table VALUES (1, 'test');

-- Invalid column definition
ALTER TABLE nonexistent_table ADD COLUMN invalid_column INVALID_TYPE;