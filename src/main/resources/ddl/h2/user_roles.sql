CREATE TABLE IF NOT EXISTS user_roles (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT,
    role_id INT, 
    userinfo_id INT
);
