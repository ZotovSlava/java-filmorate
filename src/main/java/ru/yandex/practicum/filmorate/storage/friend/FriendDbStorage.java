package ru.yandex.practicum.filmorate.storage.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NoContentException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO follows " +
                "VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, id, friendId);
        } catch (DuplicateKeyException e) {
            log.warn("Данный пользователь уже у вас в друзьях: id = {}", friendId);
            throw new ConflictException("Данный пользователь уже у вас в друзьях.");
        } catch (DataAccessException e) {
            log.error("Произошла ошибка при работе с базой данных: {}", e.getMessage());
            throw new RuntimeException("Ошибка при работе с базой данных.");
        }

        log.info("Пользователь добавлен к вам в друзья: {}", friendId);

        return true;
    }

    @Override
    public Boolean removeFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM follows " +
                "WHERE id_user_following = ? AND id_user_followed = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery, id, friendId);

        if (rowsAffected == 0) {
            throw new NoContentException("Пользователь еще не у Вас в друзьях");
        }

        log.info("Пользователь удалил пользователя из друзей");

        return true;
    }

    @Override
    public List<User> getAllFriends(Long id) {
        String sqlQuery = "SELECT users.* " +
                "FROM users " +
                "WHERE users.id IN ( " +
                "  SELECT id_user_followed " +
                "  FROM follows " +
                "  WHERE id_user_following = ?)";

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToUser, id));
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT users.* " +
                "FROM users " +
                "WHERE users.id IN ( " +
                "  SELECT id_user_followed " +
                "  FROM follows " +
                "  WHERE id_user_following = ? " +
                "  INTERSECT " +
                "  SELECT id_user_followed " +
                "  FROM follows " +
                "  WHERE id_user_following = ?)";

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId));
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setLogin(resultSet.getString("login"));
        user.setEmail(resultSet.getString("email"));
        user.setBirthday(resultSet.getDate("birthday") != null
                ? resultSet.getDate("birthday").toLocalDate() : null);

        return user;
    }
}
