package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {
    Boolean addUserLike(Long filmId, Long userId);
    Boolean removeUserLike(Long filmId, Long userId);
    List<Film> getTopLikedFilms(int count);
}
