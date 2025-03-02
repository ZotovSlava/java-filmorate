package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public Map<Long, User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Недопустимый логин");
            throw new ValidationException("Логин не должен содержать пробелов");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, устанавливается значение логина: {}", user.getLogin());
        }

        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Ошибка: не указан id для обновления пользователя.");
            throw new ValidationException("Id должен быть указан");
        }

        userStorage.getUserById(newUser.getId());

        if (newUser.getLogin().contains(" ")) {
            log.warn("Недопустимый логин");
            throw new ValidationException("Логин не должен содержать пробелов");
        }

        if (newUser.getName() == null || newUser.getName().isBlank() || newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
            log.info("Имя пользователя не указано, устанавливается значение логина: {}", newUser.getLogin());
        }

        return userStorage.updateUser(newUser);
    }

    public Boolean addFriend(Long id, Long friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        return friendStorage.addFriend(id, friendId);
    }

    public Boolean removeFriend(Long id, Long friendId) {
        userStorage.getUserById(id);
        userStorage.getUserById(friendId);

        return friendStorage.removeFriend(id, friendId);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllFriends(Long id) {
        userStorage.getUserById(id);

        return friendStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        userStorage.getUserById(id);
        userStorage.getUserById(otherId);

        return friendStorage.getCommonFriends(id, otherId);
    }
}
