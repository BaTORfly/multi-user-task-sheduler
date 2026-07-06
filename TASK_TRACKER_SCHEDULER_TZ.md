# Техническое задание: task-tracker-scheduler

## 1. Назначение сервиса

`task-tracker-scheduler` — микросервис фоновой обработки задач, реализуемый на Spring Boot 4.0.6

Сервис отвечает за ежедневный анализ пользовательских задач и публикацию email-команд в Kafka для последующей отправки писем сервисом `task-tracker-email-sender`.

Сервис должен быть stateless, не иметь собственной базы данных и работать как scheduled worker внутри микросервисной системы.

`task-tracker-scheduler` не должен отправлять email напрямую.

## 2. Роль в архитектуре

Сервис является частью системы `multi_user_task_pet_project` и взаимодействует со следующими компонентами:

```text
task-tracker-scheduler
        |
        | HTTP / service discovery
        v
task-tracker-backend
        |
        | Kafka: EMAIL_SENDING_TASKS
        v
task-tracker-email-sender
```

Зависимости сервиса:

- `task-tracker-backend` — источник пользователей и задач.
- `Kafka` — брокер сообщений для передачи email-команд.
- `task-tracker-email-sender` — consumer Kafka-сообщений и отправитель писем.
- `Config Server` — централизованная конфигурация.
- `Service Discovery` — обнаружение backend-сервиса.

## 3. Технологический стек

```text
Java 17+
Spring Boot 4.x
Spring Framework 7.x
Spring Scheduling
Spring Kafka
Spring Web / RestClient или HTTP Service Clients
Spring Cloud Config
Service Discovery client
Jackson 3
Maven
Docker
```

## 4. Функциональные требования

Сервис должен выполнять ежедневную scheduled-задачу по cron-расписанию.

Расписание должно задаваться через конфигурацию:

```yaml
scheduler:
  reports:
    daily-cron: "0 0 0 * * *"
    zone: "Europe/Moscow"
```

При запуске job сервис должен:

1. Авторизоваться в `task-tracker-backend` под техническим пользователем scheduler.
2. Получить access token. (POST http://localhost:8080/api/v1/auth/login)
3. Запросить список всех пользователей с задачами. (GET http://localhost:8080/api/v1/users)
4. Для каждого пользователя определить:
   - задачи, выполненные за отчетный период;
   - задачи, которые остаются невыполненными.
5. Сформировать email-команды.
6. Опубликовать email-команды в Kafka topic `EMAIL_SENDING_TASKS`.

Если у пользователя нет задач, письмо для него не формируется.

Если у пользователя есть и выполненные, и невыполненные задачи, сервис может сформировать два отдельных письма.

## 5. Отчетный период

Отчетный период должен рассчитываться относительно timezone, указанной в конфигурации:

```yaml
scheduler:
  reports:
    zone: "Europe/Moscow"
```

Рекомендуемый вариант:

- начало периода — начало предыдущего календарного дня в заданной timezone;
- конец периода — начало текущего календарного дня в заданной timezone.

Для ежедневной рассылки в полночь это позволяет формировать отчет строго за завершившийся день.

## 6. Контракты интеграции с backend

### 6.1. Авторизация scheduler-пользователя

Endpoint:

```http
POST /api/v1/auth/login
```

Request:

```json
{
  "email": "scheduler@example.com",
  "password": "password"
}
```

Response:

```json
{
  "access_token": "jwt-token"
}
```

### 6.2. Получение пользователей с задачами

Endpoint:

```http
GET /api/v1/users
```

Authorization:

```http
Authorization: Bearer <access_token>
```

Response:

```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "first_name": "John",
    "last_name": "Smith",
    "tasks": [
      {
        "id": 10,
        "title": "Prepare report",
        "description": "Monthly report",
        "done": true,
        "completionTime": "2026-07-06T20:15:00"
      }
    ]
  }
]
```

## 7. Kafka-контракт

### 7.1. Topic

```text
EMAIL_SENDING_TASKS
```

### 7.2. Producer

```text
task-tracker-scheduler
```

### 7.3. Consumer

```text
task-tracker-email-sender
```

### 7.4. Message format

```json
{
  "to": "user@example.com",
  "header": "You've got 3 unfinished tasks!",
  "text": "Good night, John Smith!\nYou've got 3 unfinished tasks:\n1. Task title: Task description"
}
```

### 7.5. Delivery guarantees

Рекомендуемые требования:

- сообщения должны публиковаться в Kafka в JSON-формате;
- ошибка публикации должна логироваться и отражаться в метриках;
- желательно предусмотреть retry policy для временных Kafka-ошибок;
- желательно предусмотреть dead-letter topic для сообщений, которые не удалось обработать;
- повторный запуск job не должен приводить к неконтролируемому дублированию писем за один отчетный период.

## 8. Конфигурация

Сервис должен поддерживать externalized configuration.

Пример `application.yml`:

```yaml
spring:
  application:
    name: task-tracker-scheduler

  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}

scheduler:
  credentials:
    email: ${SCHEDULER_EMAIL}
    password: ${SCHEDULER_PASSWORD}

  reports:
    daily-cron: ${SCHEDULER_DAILY_CRON:0 0 0 * * *}
    zone: ${SCHEDULER_TIME_ZONE:Europe/Moscow}
    max-tasks-in-email: ${SCHEDULER_MAX_TASKS_IN_EMAIL:5}

backend:
  service-name: task-tracker-backend
```

## 9. Обработка ошибок

Сервис должен логировать ошибки выполнения scheduled job.

Ошибки backend-интеграции, авторизации и Kafka-публикации должны быть разделены по типам.

Рекомендуемое поведение:

```text
Backend unavailable -> job failed, metric incremented
Authentication failed -> job failed, alert-worthy log
Kafka publish failed -> retry or failed metric
Invalid user/task payload -> skip invalid record or fail job according to policy
```

Для внешних вызовов и публикации сообщений желательно предусмотреть retry.

## 10. Предлагаемая структура директорий

```text
task-tracker-scheduler
├── Dockerfile
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── io
    │   │       └── github
    │   │           └── batorfly
    │   │               └── task_tracker_scheduler
    │   │                   ├── TaskTrackerSchedulerApplication.java
    │   │                   ├── config
    │   │                   │   ├── KafkaProducerConfig.java
    │   │                   │   ├── SchedulerProperties.java
    │   │                   │   ├── BackendClientProperties.java
    │   │                   │   └── ObservabilityConfig.java
    │   │                   ├── scheduler
    │   │                   │   └── DailyTaskReportScheduler.java
    │   │                   ├── client
    │   │                   │   ├── BackendClient.java
    │   │                   │   ├── BackendAuthClient.java
    │   │                   │   └── BackendUserClient.java
    │   │                   ├── service
    │   │                   │   ├── TaskReportService.java
    │   │                   │   ├── EmailReportFactory.java
    │   │                   │   └── EmailCommandPublisher.java
    │   │                   ├── dto
    │   │                   │   ├── auth
    │   │                   │   │   ├── LoginRequest.java
    │   │                   │   │   └── LoginResponse.java
    │   │                   │   ├── task
    │   │                   │   │   └── TaskDto.java
    │   │                   │   ├── user
    │   │                   │   │   └── UserWithTasksDto.java
    │   │                   │   └── email
    │   │                   │       └── EmailCommand.java
    │   │                   ├── exception
    │   │                   │   ├── BackendClientException.java
    │   │                   │   ├── SchedulerAuthenticationException.java
    │   │                   │   └── EmailPublishingException.java
    │   │                   └── util
    │   │                       └── ReportPeriodResolver.java
    │   └── resources
    │       ├── application.yaml
    └── test
        ├── java
        │   └── io
        │       └── github
        │           └── batorfly
        │               └── task_tracker_scheduler
        │                   └── integration
        │                       └── KafkaPublishingIntegrationTest.java
        └──resources
           ├── application-test.yaml
```

## 11. Описание пакетов

### 12.1. `config`

Содержит конфигурационные классы сервиса:

```text
SchedulerProperties
BackendClientProperties
KafkaProducerConfig
ObservabilityConfig
```

`SchedulerProperties` должен быть привязан к `scheduler.*` через `@ConfigurationProperties`.

`BackendClientProperties` должен описывать параметры обращения к backend.

### 12.2. `client`

Содержит HTTP-клиенты для backend API.

Ответственность:

```text
BackendAuthClient -> login scheduler-пользователя
BackendUserClient -> получение пользователей с задачами
```

Пакет `client` не должен содержать бизнес-логику анализа задач.

### 12.3. `scheduler`

Содержит классы запуска scheduled job.

Главный класс:

```text
DailyTaskReportScheduler
```

Ответственность:

```text
запустить job по расписанию
защитить выполнение от параллельного запуска при необходимости
вызвать TaskReportService
залогировать результат
обновить метрики
```

### 12.4. `service`

Содержит бизнес-логику.

```text
TaskReportService -> orchestration use case
EmailReportFactory -> формирование email-сообщений
EmailCommandPublisher -> публикация в Kafka
```

### 12.5. `dto`

Содержит DTO и внутренние модели.

Рекомендуемые модели:

```text
LoginRequest
LoginResponse
TaskDto
UserWithTasksDto
EmailCommand
```

### 12.6. `exception`

Содержит доменные исключения интеграционного слоя:

```text
BackendClientException
SchedulerAuthenticationException
EmailPublishingException
```

### 12.7. `util`

Содержит вспомогательные классы.

Пример:

```text
ReportPeriodResolver
```

## 13. Тестирование

Минимальный набор тестов:

- интеграционный тест публикации сообщения в Kafka.

Рекомендуемые тестовые классы:

```text
KafkaPublishingIntegrationTest
```

## 14. Docker

Сервис должен поставляться как Docker image.

Пример переменных окружения:

```env
SCHEDULER_DAILY_CRON=0 0 0 * * *
SCHEDULER_TIME_ZONE=Europe/Moscow
```

## 15. Критерии готовности

Сервис считается готовым, если:

```text
Scheduled job запускается по cron из конфигурации
Backend authentication работает
Пользователи с задачами успешно загружаются
Email-команды формируются корректно
Kafka-сообщения публикуются в EMAIL_SENDING_TASKS
Ошибки логируются
Конфигурация вынесена во внешние параметры
Есть интеграционный тест Kafka-публикации
Docker image собирается
```

## 16. Нефункциональные требования

- Сервис должен быть stateless.
- Сервис не должен иметь собственной базы данных.
- Сервис не должен отправлять email напрямую.
- Сервис должен быть устойчив к временной недоступности backend и Kafka.
- Сервис должен поддерживать централизованную конфигурацию.
- Сервис должен быть пригоден для запуска в Docker Compose и production-like окружении.
