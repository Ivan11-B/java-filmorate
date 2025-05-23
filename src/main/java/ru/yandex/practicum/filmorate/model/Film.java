package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validator.MinDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;
    private final Set<Long> likes = new HashSet<>();

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @MinDate(value = "1895-12-28", message = "Фильм не может быть раньше 1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    private Set<Genre> genres;

    private Mpa mpa;
}
