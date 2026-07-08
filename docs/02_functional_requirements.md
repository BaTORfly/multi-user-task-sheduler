# Functional Requirements

## Auth

| ID | Требование | Актор | Предусловия | Результат | Endpoint / Event |
| --- | --- | --- | --- | --- | --- |
| FR-AUTH-01 | Система должна регистрировать нового пользователя по имени, фамилии, email и паролю. | Guest | Email не занят, данные валидны. | Создан пользователь с ролью, возвращен access token, refresh token записан в cookie. | `POST /api/v1/auth/signup` |
| FR-AUTH-02 | Система должна отклонять регистрацию с невалидными данными. | Guest | Переданы пустые или некорректные поля. | Возвращен `400` с `ValidationErrorResponse`. | `POST /api/v1/auth/signup` |
| FR-AUTH-03 | Система должна отклонять регистрацию с уже занятым email. | Guest | Email уже есть в `users`. | Возвращен `409` с `ErrorResponse`. | `POST /api/v1/auth/signup` |
| FR-AUTH-04 | Система должна авторизовать пользователя по email и паролю. | Guest | Пользователь существует, пароль корректен. | Возвращен access token, refresh token записан в cookie. | `POST /api/v1/auth/login` |
| FR-AUTH-05 | Система должна отклонять неверные учетные данные. | Guest | Email или пароль некорректны. | Возвращен `401`. | `POST /api/v1/auth/login` |
| FR-AUTH-06 | Система должна удалять refresh cookie при logout. | User/Admin | Вызван logout. | Cookie refresh token очищен. | `POST /api/v1/auth/logout` |
| FR-AUTH-07 | Система должна выпускать новый access token по refresh token из cookie. | User/Admin | Refresh token существует, валиден и не истек. | Возвращен новый access token. | `POST /api/v1/auth/refresh-token` |

## Tasks

| ID | Требование | Актор | Предусловия | Результат | Endpoint / Event |
| --- | --- | --- | --- | --- | --- |
| FR-TASK-01 | Система должна создавать задачу для текущего пользователя. | User/Admin | Есть Bearer access token, `title` не пустой. | Создана задача владельца. | `POST /api/v1/tasks` |
| FR-TASK-02 | Система должна возвращать список задач текущего пользователя. | User/Admin | Пользователь авторизован. | Возвращен массив `TaskDto`. | `GET /api/v1/tasks` |
| FR-TASK-03 | Система должна возвращать одну задачу текущего пользователя по ID. | User/Admin | Задача существует и принадлежит пользователю. | Возвращен `TaskDto`. | `GET /api/v1/tasks/{taskId}` |
| FR-TASK-04 | Система должна обновлять задачу текущего пользователя. | User/Admin | Задача существует и принадлежит пользователю. | Данные задачи обновлены. | `PUT /api/v1/tasks/{taskId}` |
| FR-TASK-05 | Система должна удалять задачу текущего пользователя. | User/Admin | Задача существует и принадлежит пользователю. | Задача удалена, возвращен удаленный `TaskDto`. | `DELETE /api/v1/tasks/{taskId}` |
| FR-TASK-06 | Система должна запрещать доступ к чужой задаче. | User/Admin | Задача принадлежит другому пользователю. | Возвращен `403` или ошибка авторизации доступа. | `/api/v1/tasks/{taskId}` |

## Users

| ID | Требование | Актор | Предусловия | Результат | Endpoint / Event |
| --- | --- | --- | --- | --- | --- |
| FR-USER-01 | Система должна возвращать данные текущего пользователя. | User/Admin | Пользователь авторизован. | Возвращен `UserWithoutTasksDto`. | `GET /api/v1/users/current` |
| FR-USER-02 | Система должна возвращать пользователей с задачами. | Admin | Пользователь авторизован с ролью `ADMIN`. | Возвращен массив `UserWithTasksDto`. | `GET /api/v1/users` |
| FR-USER-03 | Система должна запрещать обычному пользователю получать всех пользователей. | User | Пользователь не имеет `ADMIN`. | Возвращен `403`. | `GET /api/v1/users` |

## Email и scheduler

| ID | Требование | Актор | Предусловия | Результат | Endpoint / Event |
| --- | --- | --- | --- | --- | --- |
| FR-EMAIL-01 | Система должна публиковать welcome email после успешной регистрации. | Backend | Пользователь создан. | В Kafka опубликован JSON `{to, header, text}`. | `EMAIL_SENDING_TASKS` |
| FR-EMAIL-02 | Email-sender должен читать email-команды из Kafka. | Email-sender | В topic есть сообщение. | Письмо отправлено через SMTP или ошибка залогирована. | `EMAIL_SENDING_TASKS` |
| FR-SCH-01 | Scheduler должен запускать ежедневный процесс по cron. | Scheduler | Настроены credentials, cron и timezone. | Запущено формирование отчетов. | `scheduler.reports.dailyCron` |
| FR-SCH-02 | Scheduler должен авторизоваться в backend как системный пользователь. | Scheduler | Учетные данные настроены. | Получен access token. | `POST /api/v1/auth/login` |
| FR-SCH-03 | Scheduler должен получать пользователей с задачами. | Scheduler | Получен access token с правами `ADMIN`. | Получен список пользователей с задачами. | `GET /api/v1/users` |
| FR-SCH-04 | Scheduler должен публиковать email-команды ежедневных отчетов. | Scheduler | Есть пользователи с задачами. | Команды опубликованы в Kafka. | `EMAIL_SENDING_TASKS` |

