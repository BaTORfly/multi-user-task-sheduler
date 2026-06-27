--liquibase formatted sql

--changeset batorfly:006-insert-user-tasks
INSERT INTO tasks (
    title,
    description,
    user_id,
    done,
    completion_time
)
SELECT task_data.title,
       task_data.description,
       users.user_id,
       task_data.done,
       task_data.completion_time
FROM (
    VALUES
        ('Prepare daily plan', 'Review priorities and choose the first task for the day.', 'ivan.ivanov@example.com', false, NULL::TIMESTAMPTZ),
        ('Update task notes', 'Add missing details to active work items.', 'ivan.ivanov@example.com', false, NULL::TIMESTAMPTZ),
        ('Clean inbox', 'Archive completed notifications and messages.', 'ivan.ivanov@example.com', true, NOW()),
        ('Submit weekly report', 'Send a short status report for finished work.', 'ivan.ivanov@example.com', true, NOW()),

        ('Review backlog', 'Check new items and move urgent work to the top.', 'anna.petrova@example.com', false, NULL::TIMESTAMPTZ),
        ('Plan team sync', 'Prepare agenda items for the next team meeting.', 'anna.petrova@example.com', false, NULL::TIMESTAMPTZ),
        ('Close outdated ticket', 'Mark the resolved support ticket as completed.', 'anna.petrova@example.com', true, NOW()),
        ('Publish release notes', 'Share completed release notes with the team.', 'anna.petrova@example.com', true, NOW()),

        ('Draft API checklist', 'List endpoints that need additional verification.', 'petr.sidorov@example.com', false, NULL::TIMESTAMPTZ),
        ('Verify access rules', 'Check permissions for regular user endpoints.', 'petr.sidorov@example.com', false, NULL::TIMESTAMPTZ),
        ('Fix typo in task title', 'Correct the title in the completed sample task.', 'petr.sidorov@example.com', true, NOW()),
        ('Review migration order', 'Confirm that database changes run in sequence.', 'petr.sidorov@example.com', true, NOW()),

        ('Prepare demo tasks', 'Create a short list of items for the product demo.', 'olga.smirnova@example.com', false, NULL::TIMESTAMPTZ),
        ('Check task filters', 'Verify filtering for completed and active tasks.', 'olga.smirnova@example.com', false, NULL::TIMESTAMPTZ),
        ('Archive old checklist', 'Move the finished checklist out of active work.', 'olga.smirnova@example.com', true, NOW()),
        ('Confirm notification text', 'Approve the final wording for completed task emails.', 'olga.smirnova@example.com', true, NOW())
) AS task_data(title, description, email, done, completion_time)
JOIN users ON users.email = task_data.email;

--rollback DELETE FROM tasks WHERE user_id IN (SELECT user_id FROM users WHERE email IN ('ivan.ivanov@example.com', 'anna.petrova@example.com', 'petr.sidorov@example.com', 'olga.smirnova@example.com'));
