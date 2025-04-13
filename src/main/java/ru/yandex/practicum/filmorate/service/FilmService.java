package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film add(Film film) {
        filmStorage.add(film);
        return film;
    }

    public Film update(Film newFilm) {
        filmStorage.update(newFilm);
        return newFilm;
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id).
                orElseThrow(() -> new FilmNotFoundException("Фильм не найден"));
    }

    public void addLike(Long id, Long userId) {
        if (!userStorage.checkId(userId)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        if (!userStorage.checkId(userId)) {
            throw new UserNotFoundException("User с id = " + id + " не найден");
        }
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> findPopularList(Long count) {
        return filmStorage.getPopularFilms(count);
    }
}