package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film createFilm(Film film) {
        String sqlQueryFilm = "INSERT INTO films (title, duration, description, release_date, id_mpa) " +
                "VALUES (?, ?, ?, ?, ?)";

        String sqlQueryGenre = "INSERT INTO films_genres (id_film, id_genre) " +
                "VALUES (?, ?)";

        if (film.getMpa() != null && !isValidMpa(film.getMpa().getId())) {
            log.warn("Ошибка: указан неверный id_mpa.");
            throw new NotFoundException("Такого рейтинга нет");
        }

        if (!isValidGenre(film.getGenres())) {
            log.warn("Ошибка: указан неверный id_genre.");
            throw new NotFoundException("Такого жанра нет");
        }

        List<Object[]> batchArgs = new ArrayList<>();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryFilm, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setInt(2, film.getDuration());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null) {
            for (Genre genre : new HashSet<>(film.getGenres())) {
                batchArgs.add(new Object[]{film.getId(), genre.getId()});
            }

            jdbcTemplate.batchUpdate(sqlQueryGenre, batchArgs);
        }

        return film;
    }


    @Override
    public Film updateFilm(Film newfilm) {
        String sqlQueryFilm = "UPDATE films " +
                "SET title = ?, duration = ?, description = ?, release_date = ?, id_mpa = ? " +
                "WHERE id = ?";

        String sqlQueryGenreDelete = "DELETE FROM films_genres " +
                "WHERE id_film = ?";

        String sqlQueryGenre = "INSERT INTO genre (id_film, id_genre) " +
                "VALUES (?, ?)";

        if (newfilm.getMpa() != null && !isValidMpa(newfilm.getMpa().getId())) {
            log.warn("Ошибка: указан неверный id_mpa.");
            throw new NotFoundException("Такого рейтинга нет");
        }

        if (!isValidGenre(newfilm.getGenres())) {
            log.warn("Ошибка: указан неверный id_genre.");
            throw new NotFoundException("Такого жанра нет");
        }

        List<Object[]> batchArgs = new ArrayList<>();

        jdbcTemplate.update(sqlQueryGenreDelete, newfilm.getId());

        jdbcTemplate.update(sqlQueryFilm,
                newfilm.getName(),
                newfilm.getDuration(),
                newfilm.getDescription(),
                newfilm.getReleaseDate() != null ? java.sql.Date.valueOf(newfilm.getReleaseDate()) : null,
                newfilm.getMpa().getId(),
                newfilm.getId());

        if (newfilm.getGenres() != null) {
            for (Genre genre : newfilm.getGenres()) {
                batchArgs.add(new Object[]{newfilm.getId(), genre.getId()});
            }

            jdbcTemplate.batchUpdate(sqlQueryGenre, batchArgs);
        }

        return newfilm;
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT films.*, " +
                "GROUP_CONCAT(genres.id_genre || ':' || genres.name SEPARATOR ',') AS genres, " +
                "mpa.name AS mpa_name " +
                "FROM films " +
                "LEFT JOIN mpa ON films.id_mpa = mpa.id_mpa " +
                "LEFT JOIN films_genres ON films.id = films_genres.id_film " +
                "LEFT JOIN genres ON films_genres.id_genre = genres.id_genre " +
                "WHERE films.id = ? " +
                "GROUP BY films.id, mpa.name";

        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);

        return film;
    }

    @Override
    public Map<Long, Film> findAllFilms() {
        String sqlQuery = "SELECT films.*, " +
                "GROUP_CONCAT(genres.id_genre || ':' || genres.name SEPARATOR ',') AS genres, " +
                "mpa.name AS mpa_name " +
                "FROM films " +
                "LEFT JOIN mpa ON films.id_mpa = mpa.id_mpa " +
                "LEFT JOIN films_genres ON films.id = films_genres.id_film " +
                "LEFT JOIN genres ON films_genres.id_genre = genres.id_genre " +
                "GROUP BY films.id, mpa.name";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm).stream().collect(Collectors
                .toMap(Film::getId, film -> film));
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

    private boolean isValidMpa(int mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa WHERE id_mpa = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count != null && count > 0;
    }

    private boolean isValidGenre(List<Genre> genres) {
        if (genres.isEmpty()) {
            return true;
        }

        String sql = "SELECT id_genre FROM genres";
        List<Integer> id_genres = jdbcTemplate.queryForList(sql, Integer.class);

        List<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        return id_genres.containsAll(genreIds);
    }
}
