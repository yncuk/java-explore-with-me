package ru.practicum.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    List<Integer> events;

    Boolean pinned;

    @NotBlank
    String title;
}
