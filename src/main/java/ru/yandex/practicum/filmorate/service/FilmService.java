package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    public Map<Long, Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Boolean addUserLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes (id_user, id_film) " +
                "VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DuplicateKeyException e) {
            log.warn("Нельзя оценить один и тот же фильм дважды.");
            throw new ConflictException("Данный пользователь уже оценил этот фильм.");
        } catch (DataAccessException e) {
            log.error("Произошла ошибка при работе с базой данных: {}", e.getMessage());
            throw new RuntimeException("Ошибка при работе с базой данных.");
        }

        log.info("Пользователь оценил фильм");
        return true;
    }

    public Boolean removeUserLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE id_user = ? AND id_film = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery, userId, filmId);

        if (rowsAffected == 0) {
            throw new NotFoundException("Пользователь еще не оценивал данный фильм");
        }

        log.info("Пользователь удалил свою оценку фильма");
        return true;
    }

    public List<Film> getTopLikedFilms(int count) {
        String sqlQuery = "SELECT films.*, " +
                "GROUP_CONCAT(genres.id_genre || ':' || genres.name SEPARATOR ',') AS genres, " +
                "mpa.name AS mpa_name, " +
                " COALESCE(COUNT(likes.id_user), 0) AS countLikes " +
                "FROM films " +
                "LEFT JOIN mpa ON films.id_mpa = mpa.id_mpa " +
                "LEFT JOIN likes ON films.id = likes.id_film " +
                "LEFT JOIN films_genres ON films.id = films_genres.id_film " +
                "LEFT JOIN genres ON films_genres.id_genre = genres.id_genre " +
                "GROUP BY films.id, mpa.name " +
                "ORDER BY countLikes DESC " +
                "LIMIT ?";

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count));
    }


    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getLong("id"));
        film.setDuration(resultSet.getInt("duration"));
        film.setName(resultSet.getString("title"));
        film.setDescription(resultSet.getString("description"));
        film.setMpa(new Mpa(resultSet.getInt("id_mpa"), resultSet.getString("mpa_name")));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());

        if (resultSet.getString("genres") != null) {
            String[] splitGenres = resultSet.getString("genres").split(",");

            for (String genre : splitGenres) {
                String[] splitGenreAndId = genre.split(":");
                film.getGenres().add(new Genre(Integer.parseInt(splitGenreAndId[0]), splitGenreAndId[1]));
            }
        }

        return film;
    }
}


