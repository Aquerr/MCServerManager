CREATE SCHEMA IF NOT EXISTS mcinstaller AUTHORIZATION mcinstaller;

SET SCHEMA mcinstaller;

CREATE USER IF NOT EXISTS mcinstaller PASSWORD 'mcinstaller' admin;

CREATE TABLE IF NOT EXISTS user (
   id              INT AUTO_INCREMENT              NOT NULL,
   username        VARCHAR(200)        UNIQUE      NOT NULL,
   password        VARCHAR(200)                    NOT NULL,
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ON user (id);

CREATE TABLE IF NOT EXISTS server (
    id          INT AUTO_INCREMENT                NOT NULL,
    path        VARCHAR(250)         UNIQUE        NOT NULL,
    user_id     INT                               NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX ON server (id);

-- MERGE INTO user (id, username, password) VALUES (1, 'Nerdi', '$2a$10$RsBi7zEwsAHxTgQO8cBX5Oe7iCPvIkGN3ichuibM9uGzmvx6TzFC6')

-- CREATE TABLE IF NOT EXISTS user_server (
--     user_id         INT         NOT NULL,
--     server_id       INT         NOT NULL,
--     FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
--     FOREIGN KEY (server_id) REFERENCES server(id) ON DELETE CASCADE
-- )