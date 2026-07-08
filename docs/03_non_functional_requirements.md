# Non-Functional Requirements

## Безопасность

| ID | Требование | Реализация / проверка |
| --- | --- | --- |
| NFR-SEC-01 | Защищенные endpoint должны требовать Bearer JWT. | `SecurityConfig` подключает `JwtFilter`; `/api/v1/tasks/**`, `/api/v1/users/current`, `/api/v1/users/**` защищены. |
| NFR-SEC-02 | REST API должен быть stateless. | Spring Security использует `SessionCreationPolicy.STATELESS`. |
| NFR-SEC-03 | Пароли не должны храниться в открытом виде. | Используется `BCryptPasswordEncoder`. |
| NFR-SEC-04 | Refresh token должен передаваться через cookie. | При login/signup backend пишет refresh token в response cookies. |
| NFR-SEC-05 | Swagger должен быть доступен без авторизации. | Разрешены `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`. |

## Доступность и отказоустойчивость

| ID | Требование | Реализация / проверка |
| --- | --- | --- |
| NFR-REL-01 | Email-отправка не должна блокировать основной пользовательский сценарий регистрации. | Backend публикует email-команду в Kafka, отправка выполняется отдельным сервисом. |
| NFR-REL-02 | Scheduler должен явно сообщать об ошибках интеграции с backend. | Ошибки login/users request преобразуются в `SchedulerAuthenticationException` или `BackendClientException`. |
| NFR-REL-03 | Ошибки публикации email-команд scheduler должны быть различимы. | `EmailCommandPublisher` выбрасывает `EmailPublishingException` при serialization, interrupted или execution failure. |
| NFR-REL-04 | Ошибка SMTP не должна останавливать Kafka consumer без логирования причины. | `KafkaEmailConsumer` логирует `MailException`. |

## Производительность и масштабирование

| ID | Требование | Реализация / проверка |
| --- | --- | --- |
| NFR-PERF-01 | Email-команды должны обрабатываться асинхронно. | Используется Kafka topic `EMAIL_SENDING_TASKS`. |
| NFR-PERF-02 | Consumer email-команд должен поддерживать параллельную обработку. | `@KafkaListener(..., concurrency = "3")`. |
| NFR-PERF-03 | Таблицы должны иметь индексы для частых связей и поиска. | Liquibase changes включают создание индексов в `003-add-indexes.sql`. |

## Поддерживаемость

| ID | Требование | Реализация / проверка |
| --- | --- | --- |
| NFR-MNT-01 | Конфигурация сервисов должна храниться централизованно. | Используется Spring Cloud Config Server и директория `config`. |
| NFR-MNT-02 | Сервисы должны обнаруживать друг друга по имени. | Используется Eureka Server; scheduler обращается к backend как `http://task-tracker-backend`. |
| NFR-MNT-03 | Схема БД должна версионироваться. | Используются Liquibase changelog и SQL changesets. |
| NFR-MNT-04 | Локальный запуск должен быть воспроизводимым. | Есть `docker-compose-dev.yml`, `docker-compose-prod.yml`, Dockerfile для сервисов. |

## Наблюдаемость и обработка ошибок

| ID | Требование | Реализация / проверка |
| --- | --- | --- |
| NFR-OBS-01 | API должен возвращать структурированные ошибки. | `ErrorResponse` и `ValidationErrorResponse`. |
| NFR-OBS-02 | Ошибки валидации должны содержать поля и сообщения. | `GlobalExceptionHandler` собирает `Map<String, String>` по field errors. |
| NFR-OBS-03 | Сервисы должны логировать ключевые события. | Используется `Slf4j` в backend, scheduler и email-sender. |

