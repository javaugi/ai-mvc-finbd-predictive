CREATE TABLE IF NOT EXISTS audit_logs (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50),
    region VARCHAR(32),
    description VARCHAR(255),
    audit_value float(53),
    audit_date timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP
);

