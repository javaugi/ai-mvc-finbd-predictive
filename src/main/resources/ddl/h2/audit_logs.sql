CREATE TABLE IF NOT EXISTS audit_logs (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT,
    category VARCHAR(65),
    region VARCHAR(31),
    description VARCHAR(255),
    audit_value float(53),
    audit_date timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP
);

