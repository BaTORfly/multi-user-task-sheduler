CREATE TABLE users
(
    user_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    enabled      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_time TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_time TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE roles
(
    role_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT       NOT NULL,
    role    VARCHAR(32) NOT NULL
);

CREATE TABLE tasks
(
    task_id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    user_id         BIGINT       NOT NULL,
    done            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_time    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_time    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    completion_time TIMESTAMPTZ
);

ALTER TABLE roles
    ADD CONSTRAINT fk_roles_to_user FOREIGN KEY (user_id)
        REFERENCES users ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_to_user FOREIGN KEY (user_id)
        REFERENCES users ON UPDATE RESTRICT ON DELETE CASCADE;

CREATE INDEX idx_tasks_user_id ON tasks(user_id);
