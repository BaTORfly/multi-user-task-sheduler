BEGIN;
-- Добавляем внешний ключ для таблицы roles
ALTER TABLE roles
    ADD CONSTRAINT fk_roles_to_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- Добавляем внешний ключ для таблицы tasks
ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_to_user
        FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
            ON UPDATE RESTRICT;

-- Коммитим транзакцию
COMMIT;