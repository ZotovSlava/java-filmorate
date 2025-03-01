package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@AllArgsConstructor
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms().values();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopLikedFilms(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("{id}/like/{userId}")
    public Boolean addUserLike(@PathVariable("id") Long filmId,
                               @PathVariable Long userId) {
        return filmService.addUserLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Boolean removeUserLike(@PathVariable("id") Long filmId,
                                  @PathVariable Long userId) {
        return filmService.removeUserLike(filmId, userId);
    }
}
