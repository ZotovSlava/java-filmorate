package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeStorage;

    public Map<Long, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неверная дата релиза фильма");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года");
        }

        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан id для обновления фильма.");
            throw new ValidationException("Id должен быть указан");
        }

        filmStorage.getFilmById(newFilm.getId());

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неверная дата релиза фильма");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года");
        }

        return filmStorage.updateFilm(newFilm);
    }

    public Boolean addUserLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        return likeStorage.addUserLike(filmId, userId);
    }

    public Boolean removeUserLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        return likeStorage.removeUserLike(filmId, userId);
    }

    public List<Film> getTopLikedFilms(int count) {
        return likeStorage.getTopLikedFilms(count);
    }
}


