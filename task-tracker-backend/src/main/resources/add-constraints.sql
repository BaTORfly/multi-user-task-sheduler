BEGIN;

ALTER TABLE roles
    ADD CONSTRAINT IF NOT EXISTS fk_roles_to_user
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
       ON UPDATE RESTRICT;

ALTER TABLE tasks
    ADD CONSTRAINT IF NOT EXISTS fk_tasks_to_user
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
       ON UPDATE RESTRICT;

COMMIT;