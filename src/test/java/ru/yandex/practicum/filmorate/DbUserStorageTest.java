package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
public class DbUserStorageTest {
    private final UserDbStorage userDbStorage;

    @Autowired
    public DbUserStorageTest(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Test
    public void testCreateUser() {

        User user = new User();
        user.setName("Mary");
        user.setEmail("mary@mail.ru");
        user.setLogin("superFemka");
        user.setBirthday(LocalDate.of(2010, 10, 10));
        User createdUser = userDbStorage.createUser(user);

        Assertions.assertThat(createdUser.getId()).isEqualTo(1);
        Assertions.assertThat(createdUser.getName()).isEqualTo("Mary");
        Assertions.assertThat(createdUser.getEmail()).isEqualTo("mary@mail.ru");
        Assertions.assertThat(createdUser.getLogin()).isEqualTo("superFemka");
    }
}
