CREATE TABLE IF NOT EXISTS dev_schema.user_roles (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGSERIAL, 
    userinfo_id BIGSERIAL
);
