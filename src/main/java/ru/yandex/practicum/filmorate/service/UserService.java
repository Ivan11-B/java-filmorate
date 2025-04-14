package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.create(user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        getUserById(newUser.getId());
        userStorage.update(newUser);
        return newUser;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addInFriends(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);
        userStorage.addFriends(id, friendId);
    }

    public void deleteFromFriends(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);
        userStorage.deleteFriends(id, friendId);
    }

    public Collection<User> findAllFriends(Long id) {
        getUserById(id);
        return userStorage.findAllFriends(id);
    }

    public Collection<User> findCommonFriends(Long id, Long otherId) {
        getUserById(id);
        getUserById(otherId);
        return userStorage.findCommonFriends(id, otherId);
    }
}