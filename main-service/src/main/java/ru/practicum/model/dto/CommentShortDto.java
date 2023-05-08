package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.CommentStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentShortDto {

    Integer id;

    String text;

    String authorName;

    String created;

    CommentStatus status;
}
