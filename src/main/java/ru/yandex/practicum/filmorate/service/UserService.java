package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public Map<Long, User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {

        return userStorage.updateUser(newUser);
    }

    public User addFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        if (!user.addFriend(friendId)) {
            log.warn("Данный пользователь уже у вас в друзьях: id = {}", friendId);
            throw new ConflictException("Данный пользователь уже у вас в друзьях.");
        }

        friend.addFriend(id);
        log.info("Пользователь добавлен к вам в друзья: {}", friendId);

        return user;
    }

    public User removeFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        if (!user.removeFriend(friendId)) {
            log.warn("Пользователя с таким id нет в ваших друзьях: id = {}", friendId);
            throw new ConflictException("У вас нет такого друга.");
        }

        friend.removeFriend(id);

        log.info("Пользователь удален из списка ваших друзей: {}", friendId);

        return user;
    }

    public List<User> getAllFriends(Long id) {
        User user = userStorage.getUserById(id);

        Set<Long> friendsId = user.getSetFriendsIds();

        return friendsId.stream()
                .map(userStorage.findAllUsers()::get)
                .collect(Collectors.toList());
    }


    public List<User> getCommonFriends(Long id, Long otherId) {
        Set<Long> setFriends = userStorage.getUserById(id).getSetFriendsIds();
        Set<Long> setUsersFriends = userStorage.getUserById(otherId).getSetFriendsIds();

        setFriends.retainAll(setUsersFriends);

        return setFriends.stream()
                .map(userStorage.findAllUsers()::get)
                .collect(Collectors.toList());
    }
}
