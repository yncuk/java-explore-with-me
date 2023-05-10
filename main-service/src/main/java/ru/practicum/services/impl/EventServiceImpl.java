package ru.practicum.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.exceptions.EntityForbiddenException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.enums.RequestStatus;
import ru.practicum.model.enums.State;
import ru.practicum.repositories.*;
import ru.practicum.services.EventService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> findAllByUserId(Integer userId, Integer from, Integer size) {
        validateId(userId);
        return getWitCommentsAll(eventRepository.findAllByUserId(userId)
                .stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public EventFullDto create(Integer userId, NewEventDto newEventDto) {
        validateId(userId);
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена категория с ID = %s", newEventDto.getCategory())));
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER);
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new EntityForbiddenException("Время начала события не может быть в прошлом");
        }
        Event newEvent = EventMapper.toEventFromNewEventDto(newEventDto);
        newEvent.setCategory(category);
        newEvent.setInitiator(initiator);
        newEvent.setCreatedOn(LocalDateTime.now().format(FORMATTER));
        if (newEvent.getState() == null) {
            newEvent.setState(State.PENDING);
        }
        Optional<Location> location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        location.ifPresent(newEvent::setLocation);
        if (location.isEmpty()) {
            Location newLocation = new Location();
            newLocation.setLat(newEventDto.getLocation().getLat());
            newLocation.setLon(newEventDto.getLocation().getLon());
            newEvent.setLocation(locationRepository.save(newLocation));
        }
        return EventMapper.toEventFullDtoFromEvent(eventRepository.save(newEvent));
    }

    @Override
    public EventFullDto findByUserIdAndEventId(Integer userId, Integer eventId) {
        validateId(userId);
        validateId(eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден событие с ID = %s, у пользователя с ID = %s", eventId, userId)));
        return getWithComments(EventMapper.toEventFullDtoFromEvent(event));
    }

    @Transactional
    @Override
    public EventFullDto update(Integer userId, Integer eventId, UpdateEventRequest updateEventRequest) {
        validateId(userId);
        validateId(eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден событие с ID = %s, у пользователя с ID = %s", eventId, userId)));
        LocalDateTime eventDate = event.getEventDate().minusHours(2);
        LocalDateTime nowDate = LocalDateTime.now();
        if (event.getState() != null) {
            if (event.getState().equals(State.PUBLISHED) || eventDate.isBefore(nowDate)) {
                throw new EntityForbiddenException("Неверные параметры запроса на обновление");
            }
        }
        if (updateEventRequest.getStateAction() != null) {
            if (updateEventRequest.getStateAction().equals(State.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else if (updateEventRequest.getStateAction().equals(State.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }
        }
        return getWithComments(EventMapper.toEventFullDtoFromEvent(eventRepository.save(updateCommonFields(event, updateEventRequest))));
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(Integer eventId, UpdateEventRequest updateEventRequest) {
        validateId(eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден событие с ID = %s", eventId)));
        LocalDateTime eventDate = event.getEventDate().minusHours(1);
        LocalDateTime nowDate = LocalDateTime.now();
        if (event.getState() != null) {
            if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED) || eventDate.isBefore(nowDate)) {
                throw new EntityForbiddenException("Неверные параметры запроса на обновление");
            }
        }
        if (updateEventRequest.getStateAction() != null) {
            if (updateEventRequest.getStateAction().equals(State.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now().format(FORMATTER));
            } else if (updateEventRequest.getStateAction().equals(State.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        return getWithComments(EventMapper.toEventFullDtoFromEvent(eventRepository.save(updateCommonFields(event, updateEventRequest))));
    }

    @Override
    public List<EventFullDto> findAllByParamForAdmin(Integer[] users, String[] states, Integer[] categories,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null) {
            List<BooleanExpression> listUserIds = new ArrayList<>();
            for (Integer currentUserId : users) {
                listUserIds.add(event.initiator.id.eq(currentUserId));
            }
            if (listUserIds.stream().reduce(BooleanExpression::or).isPresent()) {
                BooleanExpression listUserIdsCondition = listUserIds.stream()
                        .reduce(BooleanExpression::or)
                        .get();
                conditions.add(listUserIdsCondition);
            }
        }
        if (states != null) {
            List<BooleanExpression> listStates = new ArrayList<>();
            for (String currentState : states) {
                listStates.add(event.state.stringValue().eq(currentState));
            }
            if (listStates.stream().reduce(BooleanExpression::or).isPresent()) {
                BooleanExpression listStatesCondition = listStates.stream()
                        .reduce(BooleanExpression::or)
                        .get();
                conditions.add(listStatesCondition);
            }
        }
        if (categories != null) {
            BooleanExpression bl = getListCategoryIds(categories, event);
            if (bl != null) {
                conditions.add(bl);
            }
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.before(rangeEnd));
        }
        if (conditions.stream().reduce(BooleanExpression::and).isEmpty()) {
            return new ArrayList<>();
        }
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
        Iterable<Event> events = eventRepository.findAll(finalCondition);
        return getWitCommentsEventsFullDto(StreamSupport.stream(events.spliterator(), false).collect(Collectors.toList()))
                .stream().skip(from).limit(size).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<EventShortDto> findAllByParamPublic(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(State.PUBLISHED));

        if (text != null) {
            List<BooleanExpression> textInAnnotationOrDescription = new ArrayList<>();
            String textLowerCase = text.toLowerCase();
            textInAnnotationOrDescription.add(event.annotation.toLowerCase().like("%" + textLowerCase + "%"));
            textInAnnotationOrDescription.add(event.description.toLowerCase().like("%" + textLowerCase + "%"));
            BooleanExpression booleanExpressionText = textInAnnotationOrDescription.stream()
                    .reduce(BooleanExpression::or)
                    .get();
            conditions.add(booleanExpressionText);
        }
        if (categories != null) {
            BooleanExpression bl = getListCategoryIds(categories, event);
            if (bl != null) {
                conditions.add(bl);
            }
        }
        if (paid != null) {
            conditions.add(event.paid.eq(paid));
        }
        if (rangeStart == null && rangeEnd == null) {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.before(rangeEnd));
        }
        if (onlyAvailable != null) {
            if (onlyAvailable) {
                List<BooleanExpression> listBooleanExpression = new ArrayList<>();
                listBooleanExpression.add(event.confirmedRequests.eq(event.participantLimit));
                listBooleanExpression.add(event.participantLimit.eq(0));
                BooleanExpression listCondition = listBooleanExpression.stream()
                        .reduce(BooleanExpression::or)
                        .get();
                conditions.add(listCondition);
            }
        }
        Comparator<Event> comparator = Comparator.comparing(Event::getId);
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                comparator = Comparator.comparing(Event::getEventDate);
            } else if (sort.equals("VIEWS")) {
                comparator = Comparator.comparing(Event::getViews);
            }
        }
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
        Iterable<Event> events = eventRepository.findAll(finalCondition);
        List<Event> eventList = EventMapper.mapToEvent(events);
        for (Event currentEvent : eventList) {
            if (currentEvent.getViews() == null) {
                currentEvent.setViews(1);
            } else {
                currentEvent.setViews(currentEvent.getViews() + 1);
            }
            eventRepository.save(currentEvent);
        }
        statsClient.create("/events");
        return getWitCommentsAll(eventList.stream()
                .skip(from).limit(size).sorted(comparator).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public EventFullDto findByIdPublic(Integer id) {
        validateId(id);
        Event event = eventRepository.findByIdPublic(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдено событие с ID = %s удовлетворяющее условиям поиска", id)));
        if (event.getViews() == null) {
            event.setViews(1);
        } else {
            event.setViews(event.getViews() + 1);
        }
        eventRepository.save(event);
        statsClient.create("/events/" + id);
        return getWithComments(EventMapper.toEventFullDtoFromEvent(event));
    }

    @Override
    public List<Request> findAllRequestsByUserIdAndEventId(Integer userId, Integer eventId) {
        validateId(userId);
        validateId(eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        eventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден событие с ID = %s, у пользователя с ID = %s", eventId, userId)));
        return requestRepository.findByEvent(eventId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest request) {
        validateId(userId);
        validateId(eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден событие с ID = %s, у пользователя с ID = %s", eventId, userId)));

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            for (Integer currentRequestId : request.getRequestIds()) {
                Request currentRequest = requestRepository.findById(currentRequestId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден запрос с ID = %s", currentRequestId)));
                if (event.getParticipantLimit() != requestRepository.findByEventAndStatus(eventId, RequestStatus.CONFIRMED).size()) {
                    if (currentRequest.getStatus().equals(RequestStatus.PENDING)) {
                        if (request.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
                            currentRequest.setStatus(RequestStatus.CONFIRMED);
                            if (event.getConfirmedRequests() == null) {
                                event.setConfirmedRequests(1);
                            } else {
                                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            }
                        } else if (request.getStatus().equals(RequestStatus.REJECTED.toString())) {
                            currentRequest.setStatus(RequestStatus.REJECTED);
                        }
                    } else {
                        throw new EntityForbiddenException(String.format("Неверный статус заявки с ID = %s", currentRequestId));
                    }
                } else {
                    currentRequest.setStatus(RequestStatus.REJECTED);
                    throw new EntityForbiddenException(String.format("Достигнут лимит по заявкам на событие с ID = %s", eventId));
                }
                requestRepository.save(currentRequest);
            }
        }
        eventRepository.save(event);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(requestRepository.findByEventAndStatus(eventId, RequestStatus.CONFIRMED));
        result.setRejectedRequests(requestRepository.findByEventAndStatus(eventId, RequestStatus.REJECTED));
        return result;
    }

    private void validateId(Integer id) {
        if (id <= 0) {
            throw new EntityBadRequestException("ID должен быть больше 0");
        }
    }

    private Location getUpdateLocation(LocationDto locationDto) {
        Optional<Location> location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLat());
        if (location.isEmpty()) {
            Location newLocation = new Location();
            newLocation.setLon(locationDto.getLon());
            newLocation.setLat(locationDto.getLat());
            return locationRepository.save(newLocation);
        } else {
            return location.get();
        }
    }

    private Event updateCommonFields(Event event, UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(getUpdateLocation(updateEventRequest.getLocation()));
        }
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдена категория с ID = %s", updateEventRequest.getCategory())));
            event.setCategory(category);
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateEventRequest.getEventDate(), FORMATTER);
            if (!newEventDate.isBefore(LocalDateTime.now())) {
                event.setEventDate(newEventDate);
            } else {
                throw new EntityForbiddenException("Время начала события не может быть в прошлом");
            }
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        return event;
    }

    private BooleanExpression getListCategoryIds(Integer[] categories, QEvent event) {
        List<BooleanExpression> listCategoryIds = new ArrayList<>();
        for (Integer currentCategoryId : categories) {
            listCategoryIds.add(event.category.id.eq(currentCategoryId));
        }
        BooleanExpression listCategoryIdsCondition = null;
        if (listCategoryIds.stream().reduce(BooleanExpression::or).isPresent()) {
            listCategoryIdsCondition = listCategoryIds.stream()
                    .reduce(BooleanExpression::or)
                    .get();
        }
        return listCategoryIdsCondition;
    }

    private EventFullDto getWithComments(EventFullDto eventFullDto) {
        Optional<CounterComments> count = commentRepository.getCountCommentsByEventId(eventFullDto.getId());
        count.ifPresent(counterComments -> eventFullDto.setComments(counterComments.getCount()));
        return eventFullDto;
    }

    private List<EventShortDto> getWitCommentsAll(List<Event> list) {
        List<EventShortDto> newEvents = new ArrayList<>();
        List<CounterComments> lists = commentRepository.getCountCommentsByEvent(list);
        Map<Integer, Long> newMap = lists.stream().collect(Collectors.toMap(CounterComments::getEventId, CounterComments::getCount));
        for (Event event : list) {
            EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
            eventShortDto.setComments(newMap.getOrDefault(event.getId(), 0L));
            newEvents.add(eventShortDto);
        }
        return newEvents;
    }

    private List<EventFullDto> getWitCommentsEventsFullDto(List<Event> list) {
        List<EventFullDto> newEvents = new ArrayList<>();
        List<CounterComments> lists = commentRepository.getCountCommentsByEvent(list);
        Map<Integer, Long> newMap = lists.stream().collect(Collectors.toMap(CounterComments::getEventId, CounterComments::getCount));
        for (Event event : list) {
            EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);
            eventFullDto.setComments(newMap.getOrDefault(event.getId(), 0L));
            newEvents.add(eventFullDto);
        }
        return newEvents;
    }
}
