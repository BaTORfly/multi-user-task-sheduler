# Glossary

| Термин | Значение |
| --- | --- |
| Access token | Короткоживущий JWT, который клиент передает в `Authorization: Bearer <token>` для доступа к защищенным endpoint. |
| Refresh token | Токен для выпуска нового access token. В проекте передается через cookie. |
| JWT | JSON Web Token, формат токена с claims и подписью. |
| `USER` | Роль обычного пользователя, которому доступны операции со своими задачами и данные текущего пользователя. |
| `ADMIN` | Роль администратора, которому доступен список всех пользователей с задачами. |
| Scheduler user | Системная учетная запись, которую использует `task-tracker-scheduler` для авторизации в backend. |
| Task | Задача пользователя с названием, описанием, признаком выполнения и временем завершения. |
| `done` | Boolean-признак завершения задачи. |
| `completion_time` | Timestamp завершения задачи; используется для отбора задач в ежедневный отчет. |
| Welcome email | Письмо, которое система отправляет после успешной регистрации пользователя. |
| Daily report | Ежедневное письмо со списком задач, завершенных за предыдущий день, и незавершенных задач. |
| Email command | Сообщение `{to, header, text}`, которое producer публикует в Kafka для email-sender. |
| Kafka topic | Именованный канал Kafka. В проекте используется `EMAIL_SENDING_TASKS`. |
| Producer | Сервис, который публикует сообщение в Kafka. Producer'ы: backend и scheduler. |
| Consumer | Сервис, который читает сообщение из Kafka. Consumer: email-sender. |
| SMTP | Протокол отправки email, используемый `task-tracker-email-sender`. |
| Config Server | Spring Cloud Config Server, централизованный источник конфигурации микросервисов. |
| Service discovery | Механизм регистрации и обнаружения сервисов по имени; в проекте реализован через Eureka. |
| Eureka Server | Сервис discovery, через который микросервисы регистрируются и находят друг друга. |
| Liquibase migration | Версионированное изменение схемы БД. В проекте хранится в `db/changelog`. |
| DTO | Data Transfer Object, объект передачи данных между API, сервисами и интеграциями. |
| `ErrorResponse` | Унифицированный ответ ошибки с `message` и `timestamp`. |
| `ValidationErrorResponse` | Ответ ошибки валидации с картой ошибок по полям и `timestamp`. |
| C4 diagram | Нотация архитектурных диаграмм: system context, container, component, code. |
| REST API | HTTP API, где операции представлены endpoint, методами, request/response body и status codes. |
| Bearer token | Способ передачи access token в HTTP-заголовке `Authorization`. |
