package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FilmControllerTest {
    FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage(),
            mock(GenreStorage.class), mock(MpaStorage.class)));
    Film film;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Ivan")
                .description("ivan11fgfgfgfgfgf")
                .releaseDate(LocalDate.of(1900, 1, 1))
                .duration(30)
                .build();
    }

    @Test
    void addFilm() {
        Film newFilm = filmController.add(film);

        assertNotNull(newFilm, "Объект не создан");
        assertEquals(1, newFilm.getId(), "Id не присвоен");
    }

    @Test
    void updateFilm_DoNothing_ValidData() {
        filmController.add(film);
        Film film = Film.builder()
                .id(1L)
                .name("Ivan12")
                .description("ivan")
                .releaseDate(LocalDate.of(1950, 1, 1))
                .duration(30)
                .build();

        Film updateFilm = filmController.update(film);

        assertNotNull(updateFilm, "Объект не обновлен");

    }

    @Test
    void updateFilm_ReturnError_NotValidData() {
        filmController.add(film);
        Film film = Film.builder()
                .id(1L)
                .description("ivaneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .releaseDate(LocalDate.of(1750, 1, 1))
                .duration(-30)
                .build();

        Film updateFilm = filmController.update(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(updateFilm);

        assertFalse(violations.isEmpty());
        assertEquals(4, violations.size(), "Значение не валидных данных не совпало");

    }

    @Test
    void updateFilm_ReturnError_NotId() {
        filmController.add(film);
        boolean exeption = false;
        Film film = Film.builder()
                .name("Ivan12")
                .description("ivan")
                .releaseDate(LocalDate.of(1950, 1, 1))
                .duration(30)
                .build();
        try {
            Film updateFilm = filmController.update(film);
        } catch (ValidationException e) {
            exeption = true;
        }

        assertTrue(exeption);
    }

    @Test
    void findAll() {
        filmController.add(film);

        Collection<Film> list = filmController.findAll();

        assertNotNull(list, "Лист не создан");
        assertEquals(1, list.size(), "Количество записей не совпадает");
    }
}