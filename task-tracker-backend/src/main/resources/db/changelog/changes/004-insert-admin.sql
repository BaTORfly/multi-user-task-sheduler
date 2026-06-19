--liquibase formatted sql

--changeset batorfly:004-insert-admin
INSERT INTO users (
    first_name,
    last_name,
    email,
    password,
    enabled
)
VALUES (
           'admin',
           'scheduler',
           '${scheduler.email}',
           '${scheduler.hashed.password}',
           true
       );

INSERT INTO roles (user_id, role)
SELECT user_id, 'ADMIN'
FROM users
WHERE email = '${scheduler.email}';

INSERT INTO roles (user_id, role)
SELECT user_id, 'USER'
FROM users
WHERE email = '${scheduler.email}';