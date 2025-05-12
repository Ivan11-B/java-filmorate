package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film add(Film film) {
        if (!(film.getGenres() == null)) {
            checkGenre(film);
        }
        if (!(film.getMpa() == null)) {
            mpaStorage.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new MpaNotFoundException("Рейтинг с id = " + film.getMpa().getId() + " не найден"));
        }
        filmStorage.add(film);
        return film;
    }

    private void checkGenre(Film film) {
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            genreStorage.getGenreById(genre.getId())
                    .orElseThrow(() -> new GenreNotFoundException("Жанр с id = " + genre.getId() + " не найден"));
        }
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        getFilmById(newFilm.getId());
        filmStorage.update(newFilm);
        return newFilm;
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = " + id + " не найден"));
    }

    public void addLike(Long id, Long userId) {
        getUserById(userId);
        getFilmById(id);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        getUserById(userId);
        getFilmById(id);
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> findPopularList(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    private User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User с id = " + id + " не найден"));
    }
}