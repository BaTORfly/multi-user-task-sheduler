--liquibase formatted sql

--changeset batorfly:005-insert-users
INSERT INTO users (
    first_name,
    last_name,
    email,
    password,
    enabled
)
VALUES
    (
        'Ivan',
        'Ivanov',
        'ivan.ivanov@example.com',
        '$2a$10$mRzbdgoQUszl.7ky./1Z1.ooXMNlm1IECcIESbctRO2JnwR9GAPvO',
        true
    ),
    (
        'Anna',
        'Petrova',
        'anna.petrova@example.com',
        '$2a$10$D75ncy40DNq5PhAd1R4Tl.KerEyf1xC4gWKxQkoqAlUgyhlhrIgnK',
        true
    ),
    (
        'Petr',
        'Sidorov',
        'petr.sidorov@example.com',
        '$2a$10$xXxVlr2bE5y4uIc07Lgib.FgmD1yGLdOX/cFhACB0CQrUZCEhLPhe',
        true
    ),
    (
        'Olga',
        'Smirnova',
        'olga.smirnova@example.com',
        '$2a$10$38i.sDOiERoyhZBos.XsTOb0lWRnwaskBYC7xtTxvtDI3MflBGS5.',
        true
    );

INSERT INTO roles (user_id, role)
SELECT user_id, 'USER'
FROM users
WHERE email IN (
    'ivan.ivanov@example.com',
    'anna.petrova@example.com',
    'petr.sidorov@example.com',
    'olga.smirnova@example.com'
);

--rollback DELETE FROM users WHERE email IN ('ivan.ivanov@example.com', 'anna.petrova@example.com', 'petr.sidorov@example.com', 'olga.smirnova@example.com');
