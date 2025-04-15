package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Список пользователей предоставлен");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        userService.create(user);
        log.info("Добавлен новый пользователь");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        userService.update(newUser);
        log.info("Пользователь обновлен");
        return newUser;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен user ID={}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addInFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addInFriends(id, friendId);
        log.info("Пользователи ID={} и ID={} добавлены в друзья", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFromFriends(id, friendId);
        log.info("Пользователи ID={} и ID={} исключены из друзей", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Список друзей пользователя ID={} предоставлен", id);
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Общий список друзей пользователей ID={}, ID={} предоставлен", id, otherId);
        return userService.findCommonFriends(id, otherId);
    }
}