# 📍 Samara Audio Guide (T-Guide)

Мобильное приложение-аудиогид по достопримечательностям Самары.

## 🚀 Стек технологий
*   **Backend:** Java 17, Spring Boot 3.4, Spring Data JPA.
*   **Frontend:** React (Vite), Tailwind CSS.
*   **Database:** PostgreSQL 15 (в Docker).
*   **Mobile:** Capacitor (сборка в Android APK).

## 📂 Структура проекта
*   `/backend` — Серверная часть (API, логика, база данных).
*   `/frontend` — Клиентская часть (UI, карты, плеер).
*   `docker-compose.yml` — Конфигурация базы данных.

## 🛠 Как запустить проект

### 1. Подготовка базы данных
Убедитесь, что у вас установлен **Docker Desktop**. В корне проекта выполните: docker-compose up -d

### 2. Запуск backend
Перейдите в папку backend.
Откройте проект в IntelliJ IDEA.
Дождитесь загрузки зависимостей Gradle.
Запустите класс AudioguideApplication.
API будет доступно по адресу: http://localhost:8080
Swagger (документация): http://localhost:8080/swagger-ui.html

### 3. Запуск Frontend
Перейдите в папку frontend.
Установите зависимости: npm install
Запустите режим разработки: npm run dev
Приложение откроется на: http://localhost:5173

### 📝 Задачи и разработка
Все задачи ведутся в GitHub Projects.
При создании веток используйте именование:
feature/название-фичи
bugfix/описание-бага

### 👥 Команда
Backend: [артем], [никита], [настя]
Frontend: [сергей], [екатерина], [анна]