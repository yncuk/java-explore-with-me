package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Category;

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
}
