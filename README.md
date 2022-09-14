#Тестовое задание - проект "job4j_auth"

Учебный проект, предназначенный для реализации и демонстрации RestFull API архитектуры.
В проекте были использованы фреймворки и библиотеки Spring Boot, Spring Data, Spring WEB, Checkstyle, база данных PostgreSQL.

Команды для тестирования с помощью утилиты Curl:
1. Получаем список всех пользователей
curl -i http://localhost:8080/person/
2. Получаем данные пользователя с id = 1
curl -i http://localhost:8080/person/1
3. Создадим нового пользователя
curl -H "Content-Type:application/json" -X POST -d"{\"login\":\"job4j@gmail.com\",\"password\":\"123\"}" http://localhost:8080/person/
4. Обновим созданного пользователя
curl -i -H "Content-Type:application/json" -X PUT -d "{\"id\":\"4\",\"login\":\"support2@gmail.com\",\"password\":\"123\"}" http://localhost:8080/person/
5. И теперь удалим пользователя
curl -i -X DELETE http://localhost:8080/person/5
