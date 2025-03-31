SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';
SET NAMES utf8mb4;

USE `spring_hiber`;

INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER');

INSERT INTO users (username, password, firstname, lastname)
VALUES ('admin', 'admin', 'John', 'Doe'),
       ('user', 'user', 'Alice', 'Smith');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1);

INSERT INTO users_roles (user_id, role_id)
VALUES (2, 2);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 2);
