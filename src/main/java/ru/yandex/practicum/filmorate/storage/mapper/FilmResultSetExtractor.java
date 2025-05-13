package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, Film> filmMap = new HashMap<>();

        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.computeIfAbsent(filmId, id -> {
                try {
                    return Film.builder()
                            .id(filmId)
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .releaseDate(rs.getDate("release_date").toLocalDate())
                            .duration(rs.getInt("duration"))
                            .genres(new HashSet<>())
                            .mpa(null)
                            .build();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            if (rs.getObject("mpa_id") != null) {
                Mpa mpa = Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build();
                film.setMpa(mpa);
            }
            if (rs.getObject("genre_id") != null) {
                Genre genre = Genre.builder()
                        .id(rs.getInt("genre_id"))
                        .name(rs.getString("genre_name"))
                        .build();
                film.getGenres().add(genre);
            }
        }
        return new ArrayList<>(filmMap.values());
    }
}