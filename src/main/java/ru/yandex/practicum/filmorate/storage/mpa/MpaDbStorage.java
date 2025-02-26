package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa ORDER BY id_mpa ASC";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Mpa(rs.getInt("id_mpa"), rs.getString("name")));
    }

    @Override
    public Mpa getMpaById(Integer id) {
        try {
            String sqlQuery = "SELECT * FROM mpa WHERE id_mpa = ?";
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (rs, rowNum) -> new Mpa(rs.getInt("id_mpa"), rs.getString("name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("рейтинга с таким id нет");
        }
    }
}
