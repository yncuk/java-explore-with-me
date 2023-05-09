package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Event;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.NewEventDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class EventMapper {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getEventDate().format(FORMATTER),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews(),
                event.getComments()
        );
    }

    public List<EventShortDto> allToEventShortDto(Collection<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    public Event toEventFromNewEventDto(NewEventDto newEventDto) {
        Event newEvent = new Event();
        newEvent.setAnnotation(newEventDto.getAnnotation());
        newEvent.setDescription(newEventDto.getDescription());
        newEvent.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER));
        newEvent.setPaid(newEventDto.getPaid());
        newEvent.setParticipantLimit(newEventDto.getParticipantLimit());
        newEvent.setRequestModeration(newEventDto.getRequestModeration());
        newEvent.setTitle(newEventDto.getTitle());
        return newEvent;
    }

    public EventFullDto toEventFullDtoFromEvent(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate().format(FORMATTER),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews(),
                event.getComments()
        );
    }

    public List<EventFullDto> mapToEventFullDto(Iterable<Event> events) {
        return StreamSupport.stream(events.spliterator(), false)
                .map(EventMapper::toEventFullDtoFromEvent).collect(Collectors.toList());
    }

    public List<Event> mapToEvent(Iterable<Event> events) {
        return StreamSupport.stream(events.spliterator(), false).collect(Collectors.toList());
    }
}
