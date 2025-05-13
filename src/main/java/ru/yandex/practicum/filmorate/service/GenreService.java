package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new GenreNotFoundException("Жанр с id = " + id + " не найден"));
    }

    public Collection<Genre> findAllGenre() {
        return genreStorage.findAllGenre();
    }
}
