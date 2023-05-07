package ru.practicum.services;

import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Integer compId);

    CompilationDto update(Integer compId, NewCompilationDto newCompilationDto);

    List<CompilationDto> searchCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Integer compId);
}
