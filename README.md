#Тестовое задание - проект "job4j_auth"

Учебный проект, предназначенный для реализации и демонстрации RestFull API архитектуры.
В проекте были использованы фреймворки и библиотеки Spring Boot, Spring Data, Spring WEB, Spring Security, JWT, Checkstyle, база данных PostgreSQL

Команды для тестирования с помощью утилиты Curl:
1. Регистрируем пользователя
curl -H "Content-Type:application/json" -X POST -d"{\"login\":\"admin\",\"password\":\"qwerty\"}" http://localhost:8080/person/sign-up
2. Получаем токен пользователя
curl -i -H "Content-Type:application/json" -X POST -d {\"login\":\"admin\",\"password\":\"qwerty\"} "http://localhost:8080/login"
3. Получаем список всех пользователя с этим токеном
curl -H "Authorization: Bearer xxx.yyy.zzz" http://localhost:8080/person/all
4. Получаем с токеном пользователя по id
curl -H "Authorization: Bearer xxx.yyy.zzz" http://localhost:8080/person/1
