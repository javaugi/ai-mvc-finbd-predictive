CREATE TABLE IF NOT EXISTS prod_schema.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(65),
    email VARCHAR(255),
    password VARCHAR(255),
    name VARCHAR(65),
    first_name VARCHAR(65),
    last_name VARCHAR(65),
    middle_initial VARCHAR(1),
    birth_date TIMESTAMP WITH TIME ZONE,
    age INT,
    street_address VARCHAR(65),
    city VARCHAR(65),
    state VARCHAR(15),
    zip_code VARCHAR(15),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

