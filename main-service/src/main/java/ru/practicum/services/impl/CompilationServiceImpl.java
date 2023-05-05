package ru.practicum.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationDto;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.services.CompilationService;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation newCompilation = new Compilation();
        newCompilation.setTitle(newCompilationDto.getTitle());
        newCompilation.setPinned(newCompilationDto.getPinned());
        newCompilation.setEvents(getNewListEvents(newCompilationDto.getEvents()));
        return CompilationMapper.toCompilationDto(compilationRepository.save(newCompilation));
    }

    @Override
    public void delete(Integer compId) {
        validateId(compId);
        compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена подборка событий с ID = %s", compId)));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(Integer compId, NewCompilationDto newCompilationDto) {
        validateId(compId);
        Compilation newCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена подборка событий с ID = %s", compId)));
        if (newCompilationDto.getTitle() != null) {
            newCompilation.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.getPinned() != null) {
            newCompilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getEvents() != null) {
            newCompilation.setEvents(getNewListEvents(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(newCompilation));
    }

    @Override
    public List<CompilationDto> searchCompilations(Boolean pinned, Integer from, Integer size) {
        return CompilationMapper.allToCompilationDto(compilationRepository.findByPinnedFetch(pinned).stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Override
    public CompilationDto findById(Integer compId) {
        validateId(compId);
        return CompilationMapper.toCompilationDto(compilationRepository.findByIdFetch(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена подборка событий с ID = %s", compId))));
    }

    private void validateId(Integer id) {
        if (id <= 0) {
            throw new EntityBadRequestException("ID должен быть больше 0");
        }
    }

    private List<Event> getNewListEvents(List<Integer> oldEvents) {
        List<Event> events = new ArrayList<>();
        for (Integer currentEventId : oldEvents) {
            Event currentEvent = eventRepository.findById(currentEventId)
                    .orElseThrow(() -> new EntityBadRequestException(String.format("Не найдено событие с ID = %s", currentEventId)));
            events.add(currentEvent);
        }
        return events;
    }
}
