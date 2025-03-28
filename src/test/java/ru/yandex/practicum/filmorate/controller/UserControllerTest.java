package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController();
    User user;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Ivan")
                .login("ivan11")
                .email("rerer@mai.ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void createUser() {
        User newUser = userController.create(user);

        assertNotNull(newUser, "Объект не создан");
        assertEquals(1, newUser.getId(), "Id не присвоен");
    }

    @Test
    void updateUser_DoNothing_ValidData() {
        userController.create(user);
        User user = User.builder()
                .id(1L)
                .name("Nasty")
                .login("nasty11")
                .email("re@mail.ru")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();

        User updateUser = userController.update(user);

        assertNotNull(updateUser, "Объект не обновлен");

    }

    @Test
    void updateUser_ReturnError_NotValidData() {
        userController.create(user);
        User user = User.builder()
                .id(1L)
                .name("Nasty")
                .login("nasty11 rr")
                .email("remail.ru")
                .birthday(LocalDate.of(2110, 1, 1))
                .build();

        User updateUser = userController.update(user);
        Set<ConstraintViolation<User>> violations = validator.validate(updateUser);

        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size(), "Значение не валидных данных не совпало");

    }

    @Test
    void updateUser_ReturnError_NotId() {
        userController.create(user);
        boolean exeption = false;
        User user = User.builder()
                .name("Nasty")
                .login("nasty11")
                .email("remail.ru")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();
        try {
            User updateUser = userController.update(user);
        } catch (ValidationException e) {
            exeption = true;
        }

        assertTrue(exeption);
    }

    @Test
    void findAll() {
        userController.create(user);

        Collection<User> list = userController.findAll();

        assertNotNull(list, "Лист не создан");
        assertEquals(1, list.size(), "Количество записей не совпадает");
    }
}