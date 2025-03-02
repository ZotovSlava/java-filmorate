package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film newfilm);

    Film getFilmById(Long id);
}
