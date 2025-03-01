package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Film {
    private final List<Genre> genres = new ArrayList<>();

    private Mpa mpa;
    private Long id;
    private LocalDate releaseDate;

    @Size(max = 200, message = "Описание не может содержать более 200 символов")
    private String description;

    @Positive(message = "Продолжительность фильма строго положительное число")
    @NotNull(message = "Продолжительность не может быть равна null")
    private Integer duration;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
}
