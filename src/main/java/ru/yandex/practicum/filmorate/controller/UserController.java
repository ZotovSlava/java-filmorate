package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Недопустимый логин");
            throw new ValidationException("Логин не должен содержать пробелов");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, устанавливается значение логина: {}", user.getLogin());
        }

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь - {}, id: {}", user.getName(), user.getId());

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.warn("Ошибка: не указан id для обновления пользователя.");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка обновления несуществующего пользователя: id = {}", newUser.getId());
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getLogin().contains(" ")) {
            log.warn("Недопустимый логин");
            throw new ValidationException("Логин не должен содержать пробелов");
        }


        if (newUser.getName() == null || newUser.getName().isBlank() || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
            log.info("Имя пользователя не указано, устанавливается значение логина: {}", newUser.getLogin());
        }

        users.put(newUser.getId(), newUser);
        log.info("Обновление пользователя: id = {}, имя = {}", newUser.getId(), newUser.getName());

        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
