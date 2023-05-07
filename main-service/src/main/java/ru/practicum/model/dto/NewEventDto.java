package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;

    @NotNull
    Integer category;

    @Size(min = 20, max = 7000)
    @NotBlank
    String description;

    @NotBlank
    String eventDate;

    @NotNull
    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120)
    @NotBlank
    String title;
}
