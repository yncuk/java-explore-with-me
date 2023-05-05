package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.State;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventRequest {

    @Size(min = 20, max = 2000)
    String annotation;

    Integer category;

    @Size(min = 20, max = 7000)
    String description;

    String eventDate;

    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    State stateAction;

    @Size(min = 3, max = 120)
    String title;
}
