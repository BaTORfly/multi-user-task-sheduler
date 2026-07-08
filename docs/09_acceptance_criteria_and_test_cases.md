# Acceptance Criteria and Test Cases

## Acceptance criteria

| ID | Критерий | Связанные требования |
| --- | --- | --- |
| AC-01 | Новый пользователь может зарегистрироваться с валидными данными и получить access token. | FR-AUTH-01 |
| AC-02 | Невалидная регистрация возвращает структурированные ошибки по полям. | FR-AUTH-02 |
| AC-03 | Повторная регистрация с тем же email невозможна. | FR-AUTH-03 |
| AC-04 | Пользователь может войти по email и password. | FR-AUTH-04 |
| AC-05 | Защищенные endpoint без JWT возвращают `401`. | NFR-SEC-01 |
| AC-06 | Пользователь видит только свои задачи. | FR-TASK-02, FR-TASK-06 |
| AC-07 | Обычный пользователь не может получить `/api/v1/users`. | FR-USER-03 |
| AC-08 | После регистрации публикуется команда welcome email. | FR-EMAIL-01 |
| AC-09 | Scheduler формирует команды ежедневного отчета по пользователям с задачами. | FR-SCH-01..FR-SCH-04 |
| AC-10 | Email-sender потребляет сообщения из `EMAIL_SENDING_TASKS` и вызывает SMTP-отправку. | FR-EMAIL-02 |

## Auth test cases

| ID | Сценарий | Шаги | Ожидаемый результат | Требование |
| --- | --- | --- | --- | --- |
| TC-AUTH-01 | Успешная регистрация | Отправить валидный `SignupForm`. | `201`, `access_token`, refresh cookie. | FR-AUTH-01 |
| TC-AUTH-02 | Пустое имя | Отправить `first_name = ""`. | `400`, ошибка поля `firstName`. | FR-AUTH-02 |
| TC-AUTH-03 | Невалидный email | Отправить email без корректного формата. | `400`, ошибка поля `email`. | FR-AUTH-02 |
| TC-AUTH-04 | Слабый пароль | Отправить пароль короче 8 символов или без uppercase/lowercase. | `400`, ошибка поля `password`. | FR-AUTH-02 |
| TC-AUTH-05 | Дублирующий email | Повторить регистрацию существующего email. | `409 ErrorResponse`. | FR-AUTH-03 |
| TC-AUTH-06 | Успешный login | Отправить валидный `LoginForm`. | `200`, `access_token`, refresh cookie. | FR-AUTH-04 |
| TC-AUTH-07 | Неверный пароль | Отправить существующий email и неверный password. | `401 ErrorResponse`. | FR-AUTH-05 |
| TC-AUTH-08 | Refresh без cookie | Вызвать `/refresh-token` без cookies. | `400 ErrorResponse`. | FR-AUTH-07 |
| TC-AUTH-09 | Logout | Вызвать `/logout`. | `200`, refresh cookie очищен. | FR-AUTH-06 |

## Tasks test cases

| ID | Сценарий | Шаги | Ожидаемый результат | Требование |
| --- | --- | --- | --- | --- |
| TC-TASK-01 | Создание задачи | Отправить `POST /api/v1/tasks` с Bearer token и `title`. | `201 TaskDto`, задача принадлежит текущему пользователю. | FR-TASK-01 |
| TC-TASK-02 | Создание без title | Отправить `title = ""`. | `400 ValidationErrorResponse`. | FR-TASK-01 |
| TC-TASK-03 | Получение списка | Создать несколько задач, вызвать `GET /api/v1/tasks`. | Возвращены только задачи текущего пользователя. | FR-TASK-02 |
| TC-TASK-04 | Получение одной задачи | Вызвать `GET /api/v1/tasks/{taskId}` для своей задачи. | `200 TaskDto`. | FR-TASK-03 |
| TC-TASK-05 | Обновление задачи | Вызвать `PUT /api/v1/tasks/{taskId}`. | `200 TaskDto`, поля обновлены. | FR-TASK-04 |
| TC-TASK-06 | Удаление задачи | Вызвать `DELETE /api/v1/tasks/{taskId}`. | `200 TaskDto`, повторный поиск не возвращает задачу. | FR-TASK-05 |
| TC-TASK-07 | Доступ к чужой задаче | User A запрашивает taskId пользователя B. | `403` или доменная ошибка доступа. | FR-TASK-06 |
| TC-TASK-08 | Запрос без token | Вызвать любой `/api/v1/tasks/**` без JWT. | `401 ErrorResponse`. | NFR-SEC-01 |

## Users test cases

| ID | Сценарий | Шаги | Ожидаемый результат | Требование |
| --- | --- | --- | --- | --- |
| TC-USER-01 | Получение текущего пользователя | Вызвать `GET /api/v1/users/current` с JWT. | `200 UserWithoutTasksDto`. | FR-USER-01 |
| TC-USER-02 | Admin получает пользователей | Вызвать `GET /api/v1/users` с ролью `ADMIN`. | `200`, массив `UserWithTasksDto`. | FR-USER-02 |
| TC-USER-03 | User не получает всех пользователей | Вызвать `GET /api/v1/users` с ролью `USER`. | `403 ErrorResponse`. | FR-USER-03 |

## Integration test cases

| ID | Сценарий | Шаги | Ожидаемый результат | Требование |
| --- | --- | --- | --- | --- |
| TC-INT-01 | Welcome email event | Зарегистрировать пользователя. | В `EMAIL_SENDING_TASKS` опубликован JSON `{to, header, text}`. | FR-EMAIL-01 |
| TC-INT-02 | Email consumer | Передать корректный JSON в consumer. | `MailService.sendEmail(to, header, text)` вызван. | FR-EMAIL-02 |
| TC-INT-03 | Scheduler login | Scheduler вызывает backend login с валидными credentials. | Получен непустой access token. | FR-SCH-02 |
| TC-INT-04 | Scheduler users request | Scheduler вызывает `/api/v1/users` с Bearer token. | Получен список или пустой массив. | FR-SCH-03 |
| TC-INT-05 | Daily report publishing | Подготовить пользователя с completed yesterday и unfinished tasks. | Опубликованы email-команды отчета. | FR-SCH-04 |
| TC-INT-06 | Kafka publish failure | Сымитировать ошибку Kafka publish. | Scheduler выбрасывает `EmailPublishingException`. | NFR-REL-03 |

## Regression checklist

| Проверка | Ожидаемый результат |
| --- | --- |
| `.\mvnw.cmd test` | Все модульные и интеграционные тесты проходят. |
| Swagger UI | Открывается `/swagger-ui.html`. |
| Docker Compose dev | Поднимает инфраструктуру и сервисы в корректном порядке. |
| Mermaid diagrams | Диаграммы в `docs` рендерятся без синтаксических ошибок. |
| Secrets check | Документы не содержат реальных значений из `.env`. |

