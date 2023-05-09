package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Category;
import ru.practicum.model.enums.State;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    String annotation;

    Category category;

    Integer confirmedRequests;

    String createdOn;

    String description;

    String eventDate;

    Integer id;

    UserShortDto initiator;

    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    String publishedOn;

    Boolean requestModeration;

    State state;

    String title;

    Integer views;

    Integer comments;
}
