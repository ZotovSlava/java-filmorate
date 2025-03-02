package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    private String name;

    @NotNull(message = "Электронная почта не может быть null")
    @Email(message = "Некорректный формат почты")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String login;

    @Past(message = "Дата рождения пользователя не может быть в будущем")
    private LocalDate birthday;
}
