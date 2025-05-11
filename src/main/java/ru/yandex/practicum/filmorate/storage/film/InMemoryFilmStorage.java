package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Long id, Long userId) {
        films.get(id).getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Film> getFilmsWithGenres() {
        return null;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Genre> findAllGenre() {
        return null;
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        return null;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}