CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    category VARCHAR(65),
    region VARCHAR(31),
    description VARCHAR(255),
    audit_value DECIMAL(10,2),
    audit_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

