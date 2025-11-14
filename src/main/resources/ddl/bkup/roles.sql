CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(255) NOT NULL
);
/*
Hibernate: create table roles (id bigint not null, role varchar(255) not null, primary key (id))
*/