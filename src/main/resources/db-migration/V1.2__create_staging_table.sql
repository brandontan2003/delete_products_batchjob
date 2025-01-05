CREATE TABLE IF NOT EXISTS staging_table (
    staging_id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    status VARCHAR(15),
    scheduled_deletion_date DATE NOT NULL
);