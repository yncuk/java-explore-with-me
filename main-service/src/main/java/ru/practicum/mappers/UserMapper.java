package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.model.User;
import ru.practicum.model.dto.UserShortDto;

@Component
public class UserMapper {
    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
