package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmsStorage = new HashMap<>();
    private int nextId = 0;

    @Override
    public Map<Long, Film> getAllFilms() {
        return filmsStorage;
    }

    @Override
    public Film getFilmById(Long id) {
        return filmsStorage.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());

        filmsStorage.put(film.getId(), film);
        log.info("Добавлен новый фильм - {}, id: {}", film.getName(), film.getId());

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        filmsStorage.put(newFilm.getId(), newFilm);
        log.info("Внесены изменения в карточку фильма - {}, id: {}", newFilm.getName(), newFilm.getId());

        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = filmsStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
