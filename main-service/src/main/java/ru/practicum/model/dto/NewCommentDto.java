package ru.practicum.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    @NotNull
    @Size(min = 5, max = 2000)
    String text;

    @NotNull
    @Min(1)
    Integer eventId;
}
