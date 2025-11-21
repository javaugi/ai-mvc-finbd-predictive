CREATE TABLE IF NOT EXISTS prod_schema.user_roles (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGSERIAL, 
    userinfo_id BIGSERIAL
);
