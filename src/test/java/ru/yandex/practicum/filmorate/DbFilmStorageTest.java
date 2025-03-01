package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmDbStorage.class)
public class DbFilmStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public DbFilmStorageTest(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    @Test
    public void testCreateFilm() {

        Film film = new Film();
        film.setName("Garry");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2025, 1, 10));
        film.setMpa(new Mpa(1, "Комедия"));
        Film createdFilm = filmDbStorage.createFilm(film);

        Assertions.assertThat(createdFilm.getId()).isEqualTo(1);
        Assertions.assertThat(createdFilm.getName()).isEqualTo("Garry");
    }
}
