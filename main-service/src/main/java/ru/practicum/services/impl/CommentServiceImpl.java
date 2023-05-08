package ru.practicum.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.QComment;
import ru.practicum.model.User;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.NewCommentDto;
import ru.practicum.model.enums.CommentStatus;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.CommentService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    @Override
    public CommentDto create(Integer userId, NewCommentDto newCommentDto) {
        validateId(userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        eventRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдено событие с ID = %s", newCommentDto.getEventId())));
        Comment comment = CommentMapper.toComment(newCommentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setStatus(CommentStatus.CREATED);
        comment.setAuthor(author);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto findByUserIdAndCommentId(Integer userId, Integer commentId) {
        validateId(userId);
        validateId(commentId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Comment comment = commentRepository.findByAuthor_IdAndId(userId, commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден комментарий с ID = %s, у пользователя с ID = %s", commentId, userId)));
        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto update(Integer userId, Integer commentId, NewCommentDto newCommentDto) {
        validateId(userId);
        validateId(commentId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Comment comment = commentRepository.findByAuthor_IdAndId(userId, commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден комментарий с ID = %s, у пользователя с ID = %s", commentId, userId)));
        if (!comment.getEventId().equals(newCommentDto.getEventId())) {
            throw new EntityBadRequestException("Неверно указано события для обновления комментария");
        }
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setStatus(CommentStatus.UPDATED);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void delete(Integer userId, Integer commentId) {
        validateId(userId);
        validateId(commentId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Comment comment = commentRepository.findByAuthor_IdAndId(userId, commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден комментарий с ID = %s, у пользователя с ID = %s", commentId, userId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new EntityBadRequestException("Нельзя удалять не свой комментарий");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllComments(Integer from, Integer size) {
        return CommentMapper.allToCommentDto(commentRepository.findAll().stream().skip(from).limit(size).collect(Collectors.toList()));
    }

    @Override
    public CommentDto findById(Integer commentId) {
        validateId(commentId);
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден комментарий с ID = %s", commentId))));
    }

    @Override
    public List<Comment> findAllByParam(String text, Integer[] users, Integer[] events,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        QComment comment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        if (text != null) {
            String textLowerCase = text.toLowerCase();
            conditions.add(comment.text.toLowerCase().like("%" + textLowerCase + "%"));
        }
        if (users != null) {
            List<BooleanExpression> listUserIds = new ArrayList<>();
            for (Integer currentUserId : users) {
                listUserIds.add(comment.author.id.eq(currentUserId));
            }
            BooleanExpression listUserIdsCondition = null;
            if (listUserIds.stream().reduce(BooleanExpression::or).isPresent()) {
                listUserIdsCondition = listUserIds.stream()
                        .reduce(BooleanExpression::or)
                        .get();
            }
            if (listUserIdsCondition != null) {
                conditions.add(listUserIdsCondition);
            }
        }
        if (events != null) {
            List<BooleanExpression> listEventIds = new ArrayList<>();
            for (Integer currentEventId : events) {
                listEventIds.add(comment.eventId.eq(currentEventId));
            }
            BooleanExpression listEventIdsCondition = null;
            if (listEventIds.stream().reduce(BooleanExpression::or).isPresent()) {
                listEventIdsCondition = listEventIds.stream()
                        .reduce(BooleanExpression::or)
                        .get();
            }
            if (listEventIdsCondition != null) {
                conditions.add(listEventIdsCondition);
            }
        }
        if (rangeStart != null) {
            conditions.add(comment.created.after(LocalDateTime.parse(rangeStart, FORMATTER)));
        }
        if (rangeEnd != null) {
            conditions.add(comment.created.before(LocalDateTime.parse(rangeEnd, FORMATTER)));
        }
        if (conditions.stream().reduce(BooleanExpression::and).isEmpty()) {
            return new ArrayList<>();
        }
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
        Iterable<Comment> comments = commentRepository.findAll(finalCondition);
        return CommentMapper.mapToComment(comments).stream()
                .skip(from).limit(size).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteById(Integer commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден комментарий с ID = %s", commentId)));
        commentRepository.deleteById(commentId);
    }

    private void validateId(Integer id) {
        if (id <= 0) {
            throw new EntityBadRequestException("ID должен быть больше 0");
        }
    }
}
