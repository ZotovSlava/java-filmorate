package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Long, User> findAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    User getUserById(Long id);
}
