package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    List<EventShortDto> events;

    Integer id;

    Boolean pinned;

    String title;
}
