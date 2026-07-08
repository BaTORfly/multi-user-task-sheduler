# API Specification

Базовый URL локального backend API: `http://localhost:8080`.

Формат защищенных запросов:

```http
Authorization: Bearer <access_token>
```

## DTO

### SignupForm

```json
{
  "first_name": "John",
  "last_name": "Smith",
  "email": "john.smith@example.com",
  "password": "StrongPass123"
}
```

Правила:

| Поле | Тип | Обязательное | Правила |
| --- | --- | --- | --- |
| `first_name` | string | Да | 3-64 символа, только латинские буквы. |
| `last_name` | string | Да | 3-64 символа, только латинские буквы. |
| `email` | string | Да | Валидный email, уникален в системе. |
| `password` | string | Да | Минимум 8 символов, есть строчная и заглавная буква. |

### LoginForm

```json
{
  "email": "john.smith@example.com",
  "password": "StrongPass123"
}
```

### AuthResponseForm

```json
{
  "access_token": "<jwt>"
}
```

### TaskDto

```json
{
  "id": 1,
  "title": "Prepare report",
  "description": "Finish daily task report",
  "done": false,
  "completion_time": null
}
```

Правила:

| Поле | Тип | Обязательное | Комментарий |
| --- | --- | --- | --- |
| `id` | number | Нет | Генерируется backend. |
| `title` | string | Да | Не должен быть пустым. |
| `description` | string | Нет | Описание задачи. |
| `done` | boolean | Да | Признак завершения. |
| `completion_time` | string/null | Нет | ISO timestamp завершения задачи. |

## Auth API

| Метод | Endpoint | Auth | Назначение |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/signup` | Нет | Регистрация пользователя. |
| `POST` | `/api/v1/auth/login` | Нет | Вход пользователя. |
| `POST` | `/api/v1/auth/logout` | Нет | Очистка refresh cookie. |
| `POST` | `/api/v1/auth/refresh-token` | Refresh cookie | Выпуск нового access token. |

### POST `/api/v1/auth/signup`

Request body: `SignupForm`.

Responses:

| HTTP | Body | Условие |
| --- | --- | --- |
| `201` | `AuthResponseForm` | Пользователь создан. |
| `400` | `ValidationErrorResponse` | Ошибка валидации. |
| `409` | `ErrorResponse` | Email уже занят. |

### POST `/api/v1/auth/login`

Request body: `LoginForm`.

Responses:

| HTTP | Body | Условие |
| --- | --- | --- |
| `200` | `AuthResponseForm` | Успешный вход. |
| `400` | `ValidationErrorResponse` | Email или password пустые. |
| `401` | `ErrorResponse` | Неверные учетные данные. |

### POST `/api/v1/auth/logout`

Responses:

| HTTP | Body | Условие |
| --- | --- | --- |
| `200` | Empty | Refresh cookie очищен. |

### POST `/api/v1/auth/refresh-token`

Responses:

| HTTP | Body | Условие |
| --- | --- | --- |
| `200` | `AuthResponseForm` | Refresh token валиден. |
| `400` | `ErrorResponse` | Cookies не найдены. |
| `401` | `ErrorResponse` | Refresh token отсутствует, истек или невалиден. |

## Tasks API

Все endpoint требуют Bearer JWT и роль `USER` или `ADMIN`.

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| `POST` | `/api/v1/tasks` | Создать задачу текущего пользователя. |
| `GET` | `/api/v1/tasks` | Получить задачи текущего пользователя. |
| `GET` | `/api/v1/tasks/{taskId}` | Получить одну задачу текущего пользователя. |
| `PUT` | `/api/v1/tasks/{taskId}` | Обновить задачу текущего пользователя. |
| `DELETE` | `/api/v1/tasks/{taskId}` | Удалить задачу текущего пользователя. |

Responses:

| Сценарий | HTTP | Body |
| --- | --- | --- |
| Создание успешно | `201` | `TaskDto` |
| Получение/обновление/удаление успешно | `200` | `TaskDto` или массив `TaskDto` |
| Ошибка валидации | `400` | `ValidationErrorResponse` |
| Не авторизован | `401` | `ErrorResponse` |
| Нет доступа к задаче | `403` | `ErrorResponse` |
| Задача не найдена | `404` | `ErrorResponse` |

## Users API

| Метод | Endpoint | Auth | Назначение |
| --- | --- | --- | --- |
| `GET` | `/api/v1/users/current` | `USER` или `ADMIN` | Данные текущего пользователя без задач. |
| `GET` | `/api/v1/users` | `ADMIN` | Все пользователи с задачами. |

Responses:

| Endpoint | HTTP | Body |
| --- | --- | --- |
| `/api/v1/users/current` | `200` | `UserWithoutTasksDto` |
| `/api/v1/users` | `200` | массив `UserWithTasksDto` |
| Любой защищенный endpoint | `401` | `ErrorResponse` |
| Недостаточно прав | `403` | `ErrorResponse` |

## ErrorResponse

```json
{
  "message": "Error message",
  "timestamp": 1720000000000
}
```

## ValidationErrorResponse

```json
{
  "errors": {
    "title": "Title cannot be empty"
  },
  "timestamp": 1720000000000
}
```

