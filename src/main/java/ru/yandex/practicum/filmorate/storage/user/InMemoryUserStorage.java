package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
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
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
    }

    @Override
    public Collection<User> findAllFriends(Long id) {
        Set<Long> friendsId = users.get(id).getFriends();
        return friendsId.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(Long id, Long otherId) {
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
}