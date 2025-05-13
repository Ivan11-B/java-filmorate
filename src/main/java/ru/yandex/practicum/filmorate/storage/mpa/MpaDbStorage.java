package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaMapper mpaMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaMapper mpaMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        try {
            String query = "SELECT * FROM mpa WHERE mpa_id=?";
            return Optional.of(jdbcTemplate.queryForObject(query, mpaMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa ORDER BY mpa_id ASC", mpaMapper);
    }
}