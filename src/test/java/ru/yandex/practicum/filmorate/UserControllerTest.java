package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {

    @Test
    void validationTestSpacesInLogin() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);

        UserController userController = new UserController(userService);
        User user = new User();
        user.setLogin("ddd ddd");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1894, 8, 20));

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });

        assertEquals("Логин не должен содержать пробелов", thrown.getMessage());
    }

    @Test
    void updatingUserWhithoutID() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);

        UserController userController = new UserController(userService);
        User user = new User();
        user.setLogin("dddddd");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1894, 8, 20));

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("Id должен быть указан", thrown.getMessage());
    }
}
