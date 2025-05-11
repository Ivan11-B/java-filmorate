package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("user_id");
        String name = rs.getString("name");
        String login = rs.getString("login");
        String email = rs.getString("email");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        Set<Long> friends = new HashSet<>();
        String friendsListStr = rs.getString("friends_list");
        if (friendsListStr != null) {
            friends = Arrays.stream(friendsListStr.split(", "))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        }

        return User.builder()
                .id(userId)
                .name(name)
                .login(login)
                .email(email)
                .birthday(birthday)
                .friends(friends)
                .build();
    }
}