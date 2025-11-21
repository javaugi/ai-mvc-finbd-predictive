CREATE TABLE IF NOT EXISTS test_schema.audit_logs (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50),
    region VARCHAR(32),
    description VARCHAR(255),
    audit_value DECIMAL(10,2),
    audit_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

