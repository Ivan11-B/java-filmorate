package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class UserResultSetExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, User> idToUser = new HashMap<>();
        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            String name = rs.getString("name");
            String login = rs.getString("login");
            String email = rs.getString("email");
            LocalDate birthday = rs.getDate("birthday").toLocalDate();
            Long friend = rs.getLong("friend_id");

            if (idToUser.containsKey(userId)) {
                User existingUser = idToUser.get(userId);
                Set<Long> existingFriends = existingUser.getFriends();
                existingFriends.add(friend);
            } else {
                Set<Long> friends = new HashSet<>();
                if (!(friend == null)) {
                    friends.add(friend);
                }
                User newUser = User.builder()
                        .id(userId)
                        .name(name)
                        .login(login)
                        .email(email)
                        .birthday(birthday)
                        .friends(friends)
                        .build();
                idToUser.put(newUser.getId(), newUser);
            }
        }
        return new ArrayList<>(idToUser.values());
    }
}