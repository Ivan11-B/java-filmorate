package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        userStorage.create(user);
        return user;
    }

    public User update(User newUser) {
        userStorage.update(newUser);
        return newUser;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public void addInFriends(Long id, Long friendId) {
        userStorage.addFriends(id, friendId);
    }

    public void deleteFromFriends(Long id, Long friendId) {
        userStorage.deleteFriends(id, friendId);
    }

    public Collection<User> findAllFriends(Long id) {
        return userStorage.findAllFriends(id);
    }

    public Collection<User> findCommonFriends(Long id, Long otherId) {
        return userStorage.findCommonFriends(id, otherId);
    }
}