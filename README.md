![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Liquibase](https://img.shields.io/badge/Liquibase-2962FF?style=for-the-badge&logo=liquibase&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-2E8B57?style=for-the-badge&logo=mockito&logoColor=white)

# Система управления банковскими картами

backend-приложение на Java (Spring Boot) для управления банковскими картами:

- Создание и управление картами
- Просмотр карт
- Переводы между своими картами

## Запуск

1) Собираем исходники в jar архив: `mvn clean package -DskipTests`
2) Поднимаем БД Postgres в контейнере докер: `docker compose up -d`
3) Запускаем jar архив: `java -jar target/bank-rest-0.0.1-SNAPSHOT.jar`

Приложение запустится на порту 8080 и будет доступно по адресу http://localhost:8080

После запуска приложения будет доступен Swagger UI по адресу http://localhost:8080/swagger-ui.html

OpenAPI документация доступна по адресу http://localhost:8080/api-docs и также локально в файле [./docs/openapi.yaml](./docs/openapi.yaml)

## 💳 Атрибуты карты

- Номер карты (зашифрован, отображается маской: `**** **** **** 1234`)
- Владелец
- Срок действия
- Статус: Активна, Заблокирована, Истек срок
- Баланс

### ✅ Аутентификация и авторизация - _в разработке_

- Spring Security + JWT
- Роли: **ADMIN** и **USER**

### ✅ Возможности

**Администратор:**
- Создаёт, блокирует, активирует, удаляет карты
- Управляет пользователями
- Видит все карты

**Пользователь:**
- Просматривает свои карты (поиск + пагинация)
- Запрашивает блокировку карты
- Делает переводы между своими картами
- Смотрит баланс

### ✅ API

- CRUD для карт
- Переводы между своими картами
- Фильтрация и постраничная выдача
- Валидация и сообщения об ошибках

### ✅ Безопасность

- Шифрование данных - _в разработке_
- Ролевой доступ - _в разработке_
- Маскирование номеров карт

### ✅ Работа с БД

- PostgreSQL - поднимается в докер контейнере, доступен на порту 5433. Volume для хранения данных - [./db-data](./db-data). [Схема данных](./docs/ER-diagram.png) 
- Миграции через Liquibase (`src/main/resources/db/migration`)
