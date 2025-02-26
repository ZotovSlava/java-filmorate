package ru.yandex.practicum.filmorate.storage.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres ORDER BY id_genre ASC";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(rs.getInt("id_genre"), rs.getString("name")));
    }

    @Override
    public Genre getGenreById(Integer id) {

        try {
            String sqlQuery = "SELECT * FROM genres WHERE id_genre = ?";
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (rs, rowNum) -> new Genre(rs.getInt("id_genre"), rs.getString("name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("жанра с таким id нет");
        }
    }
}
