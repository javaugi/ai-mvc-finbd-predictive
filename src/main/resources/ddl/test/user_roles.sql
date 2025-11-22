CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT,
    role_id BIGSERIAL, 
    userinfo_id BIGSERIAL
);
