package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms().values();
    }

    @GetMapping("/popular")
    public Collection<Film> getTopLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopLikedFilms(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неверная дата релиза фильма");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года");
        }

        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка: не указан id для обновления фильма.");
            throw new ValidationException("Id должен быть указан");
        }

        if (!filmService.findAllFilms().containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего фильма: id = {}", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Неверная дата релиза фильма");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года");
        }

        return filmService.updateFilm(newFilm);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addUserLike(@PathVariable("id") Long filmId,
                            @PathVariable Long userId) {

        if (!filmService.findAllFilms().containsKey(filmId)) {
            log.warn("Фильма с таким id нет: id = {}", filmId);
            throw new NotFoundException("Фильма с id = " + filmId + " не найден");
        }

        if (!userService.findAllUsers().containsKey(userId)) {
            log.warn("Пользователя с таким id нет: id = {}", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return filmService.addUserLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film removeUserLike(@PathVariable("id") Long filmId,
                               @PathVariable Long userId) {
        if (!filmService.findAllFilms().containsKey(filmId)) {
            log.warn("Фильма с таким id нет: id = {}", filmId);
            throw new NotFoundException("Фильма с id = " + filmId + " не найден");
        }

        if (!userService.findAllUsers().containsKey(userId)) {
            log.warn("Пользователя с таким id нет: id = {}", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        return filmService.removeUserLike(filmId, userId);
    }
}
