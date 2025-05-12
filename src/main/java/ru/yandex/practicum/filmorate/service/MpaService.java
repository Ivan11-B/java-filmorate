package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new MpaNotFoundException("Рейтинг с id = " + id + " не найден"));
    }

    public Collection<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }
}
