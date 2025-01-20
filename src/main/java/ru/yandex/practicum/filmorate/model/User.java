package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;
    String name;

    @NotNull (message = "Электронная почта не может быть null")
    @Email(message = "Некорректный формат почты")
    String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    String login;

    @Past(message = "Дата рождения пользователя не может быть в будущем")
    LocalDate birthday;
}
