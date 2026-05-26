--liquibase formatted sql

--changeset batorfly:003-add-indexes

-- Индекс для tasks.user_id (JOIN + DELETE CASCADE)
CREATE INDEX idx_tasks_user_id ON tasks(user_id);

-- Индекс для roles.user_id (JOIN + DELETE CASCADE)
CREATE INDEX idx_roles_user_id ON roles(user_id);

--rollback
DROP INDEX IF EXISTS idx_tasks_user_id;
DROP INDEX IF EXISTS idx_roles_user_id;

--rollback DROP INDEX IF EXISTS idx_tasks_user_id;
--rollback DROP INDEX IF EXISTS idx_roles_user_id;