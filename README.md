# ☕ Cafe CRM

REST API для автоматизации работы кафе: управление заказами, меню, бронированием столиков, персоналом и аналитикой.

## 🎯 Возможности

- **Заказы** — создание, закрытие, отмена; поддержка типов `DINE_IN` (в зале) и `DELIVERY` (доставка)
- **Меню** — категории и позиции (блюда/напитки) с управлением доступностью
- **Столики** — управление расположением и статусом (FREE / OCCUPIED / RESERVED)
- **Бронирование** — бронь столиков с проверкой конфликтов по времени
- **Кухня & Бар** — отдельные очереди позиций заказа с отслеживанием статуса приготовления
- **Аналитика** — средний чек, почасовая загрузка, популярные блюда
- **Аутентификация** — JWT через cookie; ролевая модель (ADMIN, WAITER, COOK, BARTENDER, CLIENT)

## 🛠 Стек

### Backend
| Технология | Версия | Назначение |
|------------|--------|------------|
| Java | 21 | Язык |
| Spring Boot | 3.x | Фреймворк |
| Spring Security | 3.x | JWT-аутентификация, авторизация |
| Spring Data JPA | 3.x | Работа с БД |
| PostgreSQL | 15+ | Основная БД |
| MapStruct | 1.5+ | Маппинг DTO ↔ Entity |
| Lombok | 1.18+ | Генерация boilerplate |
| SpringDoc OpenAPI | 2.6+ | Swagger UI документация |

### Frontend (в разработке)
| Технология | Версия | Назначение |
|------------|--------|------------|
| React | 18+ | UI-фреймворк |
| TypeScript | 5.x | Типизация |
| Vite | 5.x | Сборка |
| Ant Design / Tailwind CSS | — | UI-компоненты и стилизация |

## 📡 API Endpoints

### 🔐 Authentication
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `POST` | `/api/auth/register/client` | Публичный | Регистрация клиента |
| `POST` | `/api/auth/register/staff` | ADMIN | Регистрация сотрудника |
| `POST` | `/api/auth/login/client` | Публичный | Вход клиента (JWT cookie) |
| `POST` | `/api/auth/login/staff` | Публичный | Вход сотрудника (JWT cookie) |
| `POST` | `/api/auth/logout` | Аутентифицированный | Выход (инвалидация cookie) |

### 📊 Analytics
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `GET` | `/api/analytics/average-check` | ADMIN | Средний чек за период |
| `GET` | `/api/analytics/hourly-load` | ADMIN | Почасовая загрузка (0-23) |
| `GET` | `/api/analytics/popular-items` | ADMIN | Популярные блюда (с пагинацией) |

### 🪑 Tables
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `GET` | `/api/tables` | Публичный | Все столики |
| `POST` | `/api/tables` | Публичный | Создать столик |
| `PATCH` | `/api/tables/{id}/position` | Публичный | Обновить позицию |
| `PATCH` | `/api/tables/{id}/status` | Публичный | Обновить статус |
| `DELETE` | `/api/tables/{id}` | Публичный | Удалить столик |

### 📅 Reservations
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `GET` | `/api/reservations` | ADMIN | Все бронирования |
| `GET` | `/api/reservations/{id}` | ADMIN | Бронирование по ID |
| `GET` | `/api/reservations/available-tables` | ADMIN | Доступные столики на интервал |
| `POST` | `/api/reservations` | ADMIN / WAITER | Создать бронирование |
| `PUT` | `/api/reservations/{id}/cancel` | Аутентифицированный | Отменить бронирование |

### 🍽 Menu
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `GET` | `/api/menu` | Аутентифицированный | Все категории с позициями |
| `GET` | `/api/menu/items` | Аутентифицированный | Позиции по ID категории |
| `POST` | `/api/menu/categories` | ADMIN / COOK / BARTENDER | Создать категорию |
| `POST` | `/api/menu/items` | ADMIN / COOK / BARTENDER | Создать позицию |
| `PUT` | `/api/menu/items/{id}` | ADMIN / COOK / BARTENDER | Обновить позицию |
| `DELETE` | `/api/menu/categories/{id}` | ADMIN / COOK / BARTENDER | Удалить категорию |
| `DELETE` | `/api/menu/items/{id}` | ADMIN / COOK / BARTENDER | Удалить позицию |

### 📋 Orders
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `GET` | `/api/orders` | ADMIN | Все заказы (фильтр по status) |
| `GET` | `/api/orders/{id}` | ADMIN | Заказ по ID |
| `GET` | `/api/orders/table/{tableId}` | ADMIN / WAITER | Заказы по столику |
| `GET` | `/api/orders/client/{clientId}` | ADMIN / WAITER | Заказы по клиенту |
| `POST` | `/api/orders` | Аутентифицированный | Создать заказ |
| `PUT` | `/api/orders/{id}/close` | Аутентифицированный | Закрыть заказ (оплата) |
| `PUT` | `/api/orders/{id}/cancel` | Аутентифицированный | Отменить заказ |

### 🍳 Order Items (Kitchen & Bar)
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| `GET` | `/api/orders/items/kitchen` | Аутентифицированный | Позиции для кухни |
| `GET` | `/api/orders/items/bar` | Аутентифицированный | Позиции для бара |
| `PUT` | `/api/orders/items/{id}/status` | ADMIN / COOK / BARTENDER | Обновить статус позиции |

&gt; 📘 Полная интерактивная документация доступна по адресу `/swagger-ui.html` после запуска приложения.

## 🚀 Запуск через Docker Compose

### Предварительные требования
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### 1. Клонировать репозиторий
```bash
git clone https://github.com/AinLume/LegendaryApp.git
```

### Настроить переменные окружения
Создай файл .env в корне проекта (или используй defaults):
env

# Database
```bash
POSTGRES_DB=cafecrm
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```
