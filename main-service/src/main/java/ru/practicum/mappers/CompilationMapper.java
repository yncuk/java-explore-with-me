package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.model.Compilation;
import ru.practicum.model.dto.CompilationDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                EventMapper.allToEventShortDto(compilation.getEvents()),
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }

    public static List<CompilationDto> allToCompilationDto(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }
}
