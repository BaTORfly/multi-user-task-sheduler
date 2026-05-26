--liquibase formatted sql

--changeset batorfly:002-add-constraints

ALTER TABLE roles
    ADD CONSTRAINT fk_roles_to_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

ALTER TABLE roles
    ADD CONSTRAINT chk_roles_role
        CHECK (role IN ('USER', 'ADMIN'));

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_to_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

--rollback ALTER TABLE tasks DROP CONSTRAINT IF EXISTS fk_tasks_to_user;
--rollback ALTER TABLE roles DROP CONSTRAINT IF EXISTS chk_roles_role;
--rollback ALTER TABLE roles DROP CONSTRAINT IF EXISTS fk_roles_to_user;


