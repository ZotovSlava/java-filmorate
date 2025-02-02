package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersStorage = new HashMap<>();
    private int nextId = 0;

    @Override
    public Map<Long, User> findAllUsers() {
        return usersStorage;
    }

    @Override
    public User getUserById(Long id) {
        return usersStorage.get(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());

        usersStorage.put(user.getId(), user);
        log.info("Добавлен новый пользователь - {}, id: {}", user.getName(), user.getId());

        return user;
    }

    @Override
    public User updateUser(User newUser) {
        usersStorage.put(newUser.getId(), newUser);
        log.info("Обновление пользователя: id = {}, имя = {}", newUser.getId(), newUser.getName());

        return newUser;
    }

    private long getNextId() {
        long currentMaxId = usersStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
