package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public Map<Long, Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film addUserLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (!film.addUserLikeId(userId)) {
            log.warn("Нельзя оценить один и тот же фильм дважды.");
            throw new ConflictException("Данный пользователь уже оценил этот фильм.");
        }

        log.info("Пользователь оценил фильм");

        return film;
    }

    public Film removeUserLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (!film.removeUserLikeId(userId)) {
            throw new ConflictException("Данный пользователь еще не оценивал этот фильм.");
        }

        log.info("Пользователь удалил свою оценку фильма");

        return film;
    }

    public List<Film> getTopLikedFilms(int count) {
        return filmStorage.findAllFilms().values()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getSetUsersLikeIds().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
