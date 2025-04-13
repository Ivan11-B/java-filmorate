package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            return newFilm;
        }
        throw new FilmNotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return films.values().stream()
                .filter(film -> Objects.equals(film.getId(), id))
                .findFirst();
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (!checkId(id)) {
            throw new FilmNotFoundException("Film с id = " + id + " не найден");
        }
        films.get(id).getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        if (!checkId(id)) {
            throw new FilmNotFoundException("Film с id = " + id + " не найден");
        }
        films.get(id).getLikes().remove(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean checkId(Long id) {
        return films.containsKey(id);
    }
}