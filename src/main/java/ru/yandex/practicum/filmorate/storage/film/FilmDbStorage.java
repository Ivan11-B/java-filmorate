package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final GenreMapper genreMapper;
    private final MpaMapper mpaMapper;
    private final FilmResultExtractor filmResultExtractor;
    private final FilmResultSetExtractor filmResultSetExtractor;


    private static final String QUERY_ALL_FILMS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, g.genre_id ";

    private static final String QUERY_FILM =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "WHERE f.film_id = ? " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, g.genre_id ";


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmMapper, GenreMapper genreMapper, MpaMapper mpaMapper, FilmResultExtractor filmResultExtractor, FilmResultSetExtractor filmResultSetExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmMapper;
        this.genreMapper = genreMapper;
        this.mpaMapper = mpaMapper;
        this.filmResultExtractor = filmResultExtractor;
        this.filmResultSetExtractor = filmResultSetExtractor;
    }

    @Override
    public Film add(Film film) {
        String query = "INSERT INTO Films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
        addGenreToFilm(film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(QUERY_ALL_FILMS, filmResultSetExtractor);
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        String query = "UPDATE Films SET name=?, description=?, release_date=?, duration=?, mpa_id=? " +
                "WHERE film_id=?";
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate()
                , film.getDuration(), film.getId(), film.getMpa().getId());
        addGenreToFilm(film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {

        return Optional.of(jdbcTemplate.query(QUERY_FILM, filmResultExtractor, id));
    }

    @Override
    public void addLike(Long id, Long userId) {
        String query = "INSERT INTO Likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(query, id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        String query = "DELETE FROM Likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(query, id, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        String query = "SELECT f.*, COUNT(l.user_id) AS likes_count " +
                "FROM films AS f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(query, filmMapper, count);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        try {
            String query = "SELECT * FROM genres WHERE genre_id=?";
            return Optional.of(jdbcTemplate.queryForObject(query, genreMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getFilmsWithGenres() {
        return null;
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
    public Collection<Genre> findAllGenre() {
        return jdbcTemplate.query("SELECT * FROM Genres ORDER BY genre_id ASC", genreMapper);
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa ORDER BY mpa_id ASC", mpaMapper);
    }

    private void addGenreToFilm(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());

            String insertGenreSql = "MERGE INTO film_genre KEY (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.batchUpdate(insertGenreSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Genre genre = new ArrayList<>(uniqueGenres).get(i);
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                }

                @Override
                public int getBatchSize() {
                    return uniqueGenres.size();
                }
            });
        }
    }
}