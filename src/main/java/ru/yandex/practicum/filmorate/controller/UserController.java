package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers().values();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findUserFriends(@PathVariable Long id) {
        if (!userService.findAllUsers().containsKey(id)) {
            log.warn("Пользователя с таким id нет: id = {}", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable Long id,
                                              @PathVariable Long otherId) {
        if (!userService.findAllUsers().containsKey(id)) {
            log.warn("Пользователя с таким id нет: id = {}", id);
            throw new ValidationException("Пользователь с id = " + id + " не найден");
        }

        if (!userService.findAllUsers().containsKey(otherId)) {
            log.warn("Пользователя с таким id нет: otherId = {}", otherId);
            throw new ValidationException("Пользователь с otherId = " + otherId + " не найден");
        }

        return userService.getCommonFriends(id, otherId);
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

        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Ошибка: не указан id для обновления пользователя.");
            throw new ValidationException("Id должен быть указан");
        }

        if (!userService.findAllUsers().containsKey(newUser.getId())) {
            log.warn("Попытка обновления несуществующего пользователя: id = {}", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getLogin().contains(" ")) {
            log.warn("Недопустимый логин");
            throw new ValidationException("Логин не должен содержать пробелов");
        }

        if (newUser.getName() == null || newUser.getName().isBlank() || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
            log.info("Имя пользователя не указано, устанавливается значение логина: {}", newUser.getLogin());
        }

        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {

        if (!userService.findAllUsers().containsKey(id)) {
            log.warn("Пользователя с таким id нет: id = {}", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        if (!userService.findAllUsers().containsKey(friendId)) {
            log.warn("Попытка добавить в друзья несуществующего пользователя: id = {}", friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Long id,
                             @PathVariable Long friendId) {
        if (!userService.findAllUsers().containsKey(id)) {
            log.warn("Пользователя с таким id нет: id = {}", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        if (!userService.findAllUsers().containsKey(friendId)) {
            log.warn("Пользователя с таким friendId нет: id = {}", friendId);
            throw new NotFoundException("Пользователь с friendId = " + friendId + " не найден");
        }

        return userService.removeFriend(id, friendId);
    }
}
