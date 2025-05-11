package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;


    private static final String QUERY_ALL_USERS =
            "SELECT u.user_id, u.name, u.login, u.email, u.birthday, STRING_AGG(f.friend_id::TEXT, ', ') AS friends_list " +
                    "FROM users u " +
                    "LEFT JOIN friends f ON u.user_id = f.user_id " +
                    "GROUP BY u.user_id, u.name, u.login, u.email, u.birthday";

    private static final String QUERY_USER =
            "SELECT u.user_id, u.name, u.login, u.email, u.birthday, STRING_AGG(f.friend_id::TEXT, ', ') AS friends_list " +
                    "FROM users u " +
                    "LEFT JOIN friends f ON u.user_id = f.user_id " +
                    "WHERE u.user_id = ? " +
                    "GROUP BY u.user_id, u.name, u.login, u.email, u.birthday";


    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users (name, login, email, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(QUERY_ALL_USERS, userMapper);
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET name=?, login=?, email=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(query, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        try {
            String query = "SELECT * " +
                    "FROM users AS u " +
                    "LEFT JOIN friends AS f ON u.user_id = f.user_id " +
                    "WHERE u.user_id = ? ";

            return Optional.ofNullable(jdbcTemplate.queryForObject(QUERY_USER, userMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        String query = "MERGE INTO friends KEY (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(query, id, friendId);
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        String query = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(query, id, friendId);

    }

    @Override
    public Collection<User> findAllFriends(Long id) {
        Collection<User> friends = new ArrayList<>();
        String query = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Long> friendsList = jdbcTemplate.queryForList(query, Long.class, id);
        for (Long friend : friendsList) {
            Optional<User> user = getUserById(friend);
            user.ifPresent(friends::add);
        }
        return friends;
    }

    @Override
    public Collection<User> findCommonFriends(Long id, Long otherId) {
        Collection<User> commonFriends = new ArrayList<>();
        String query = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Long> friendsList = jdbcTemplate.queryForList(query, Long.class, id);
        List<Long> friendsListOther = jdbcTemplate.queryForList(query, Long.class, otherId);

        friendsList.retainAll(friendsListOther);
        for (Long friend : friendsList) {
            Optional<User> user = getUserById(friend);
            user.ifPresent(commonFriends::add);
        }
        return commonFriends;
    }
}