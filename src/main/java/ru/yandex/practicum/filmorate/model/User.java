package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @Setter(lombok.AccessLevel.NONE)
    private final Set<Long> setFriendsIds = new HashSet<>();

    private Long id;
    private String name;

    @NotNull(message = "Электронная почта не может быть null")
    @Email(message = "Некорректный формат почты")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String login;

    @Past(message = "Дата рождения пользователя не может быть в будущем")
    private LocalDate birthday;

    public boolean addFriend(Long friendId) {
        return setFriendsIds.add(friendId);
    }

    public boolean removeFriend(Long friendId) {
        return setFriendsIds.remove(friendId);
    }
}
