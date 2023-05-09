package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.model.User;
import ru.practicum.model.dto.UserShortDto;

@UtilityClass
public class UserMapper {
    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
