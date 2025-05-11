package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Список фильмов предоставлен");
        return filmService.findAll();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        filmService.add(film);
        log.info("Добавлен новый фильм");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        filmService.update(newFilm);
        log.info("Фильм обновлен");
        return newFilm;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен фильм ID={}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь ID={} добавил like фильму ID={}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        log.info("Пользователь ID={} удалил like фильму ID={}", userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularList(@RequestParam(name = "count", required = false, defaultValue = "10") Long count) {
        log.info("ID={} популярных фильмов предоставлен", count);
        return filmService.findPopularList(count);
    }

    @GetMapping("/genres")
    public Collection<Film> getFilmsWithGenres() {
        log.info("Фильмы c жанрами");
        return filmService.findFilmsWithGenres();
    }
}