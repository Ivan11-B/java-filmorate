package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private Set<Long> friends;

    @NotBlank(message = "Поле login не должно быть пустым" + "\n")
    @Pattern(message = "В поле login не должны быть пробелы" + "\n", regexp = "^[0-9A-Za-z]{6,16}$")
    private String login;

    @NotBlank(message = "Поле e-mail не должно быть пустым" + "\n")
    @Email(message = "Не верный формат e-mail", regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}