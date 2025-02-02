package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmControllerTest {

    @Test
    void validationTestReleaseDate() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage);

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);

        FilmController filmController = new FilmController(filmService,userService);
        Film film = new Film();
        film.setName("Фильм");
        film.setDuration(20);
        film.setDescription("111111111");
        film.setReleaseDate(LocalDate.of(1766, 8, 20));

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        assertEquals("Дата релиза фильма - не раньше 28 декабря 1895 года", thrown.getMessage());
    }

    @Test
    void updatingFilmWhithoutID() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage);

        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);

        FilmController filmController = new FilmController(filmService,userService);
        Film film = new Film();
        film.setName("Фильм");
        film.setDuration(20);
        film.setDescription("111111111");
        film.setReleaseDate(LocalDate.of(1766, 8, 20));

        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            filmController.updateFilm(film);
        });

        assertEquals("Id должен быть указан", thrown.getMessage());
    }
}
