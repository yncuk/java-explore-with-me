package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.CommentStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    Integer id;

    String text;

    Integer eventId;

    UserShortDto author;

    String created;

    CommentStatus status;

    String updated;
}
