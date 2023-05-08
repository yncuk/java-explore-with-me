package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Category;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    String annotation;

    Category category;

    Integer confirmedRequests;

    String eventDate;

    Integer id;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Integer views;

    List<CommentShortDto> comments;
}
