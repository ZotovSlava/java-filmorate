package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неверная дата релиза фильма");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Добавлен новый фильм - {}, id: {}", film.getName(), film.getId());

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан id для обновления фильма.");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего фильма: id = {}", newFilm.getId());
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неверная дата релиза фильма");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года");
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Внесены изменения в карточку фильма - {}, id: {}", newFilm.getName(), newFilm.getId());

        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
