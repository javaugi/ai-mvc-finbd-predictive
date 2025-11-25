CREATE TABLE privileges (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    name VARCHAR(65) NOT NULL UNIQUE
);
