package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @Setter(lombok.AccessLevel.NONE)
    private final Set<Long> setUsersLikeIds = new HashSet<>();

    Long id;
    LocalDate releaseDate;

    @Size(max = 200, message = "Описание не может содержать более 200 символов")
    String description;

    @Positive(message = "Продолжительность фильма строго положительное число")
    @NotNull(message = "Продолжительность не может быть равна null")
    Integer duration;

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    public boolean addUserLikeId(Long id) {
        return setUsersLikeIds.add(id);
    }

    public boolean removeUserLikeId(Long id) {
        return setUsersLikeIds.remove(id);
    }
}
