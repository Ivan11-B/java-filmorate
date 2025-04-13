package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            users.put(newUser.getId(), newUser);
            return newUser;
        }
        throw new UserNotFoundException("User с id = " + newUser.getId() + " не найден");
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return users.values().stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        if (!checkId(id)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");
        }
        if (!checkId(friendId)) {
            throw new UserNotFoundException("Friends с id = " + id + " не найден");
        }
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        if (!checkId(id)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");
        }
        if (!checkId(friendId)) {
            throw new UserNotFoundException("Friends с id = " + id + " не найден");
        }
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
    }

    @Override
    public Collection<User> findAllFriends(Long id) {
        if (!checkId(id)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");
        }
        Set<Long> friendsId = users.get(id).getFriends();
        return friendsId.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(Long id, Long otherId) {
        if (!checkId(id)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");
        }
        if (!checkId(otherId)) {
            throw new UserNotFoundException("Другой user с id = " + id + " не найден");
        }
        Set<Long> friendsId = users.get(id).getFriends();
        Set<Long> friendsOtherId = users.get(otherId).getFriends();
        return friendsId.stream()
                .filter(friendsOtherId::contains)
                .map(users::get)
                .collect(Collectors.toSet());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public boolean checkId(Long id) {
        return users.containsKey(id);
    }
}
