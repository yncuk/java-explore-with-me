package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Compilation;
import ru.practicum.model.dto.CompilationDto;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                EventMapper.allToEventShortDto(compilation.getEvents()),
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }

    public List<CompilationDto> allToCompilationDto(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }
}
