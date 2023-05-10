package ru.practicum.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.exceptions.EntityForbiddenException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.QComment;
import ru.practicum.model.User;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.NewCommentDto;
import ru.practicum.model.enums.CommentStatus;
import ru.practicum.model.enums.State;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.services.CommentService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentDto create(Integer userId, NewCommentDto newCommentDto) {
        validateId(userId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден пользователь с ID = %s", userId)));
        Event event = eventRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найдено событие с ID = %s", newCommentDto.getEventId())));
        if (event.getState() == null) {
            throw new EntityForbiddenException("Нельзя оставлять комментарии под неопубликованным событием");
        } else if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityForbiddenException("Нельзя оставлять комментарии под неопубликованным событием");
        }
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
        if (comment.getCreated().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new EntityBadRequestException("Нельзя редактировать комментарии, которым больше часа");
        }
        comment.setText(newCommentDto.getText());
        comment.setUpdated(LocalDateTime.now());
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
    public List<CommentDto> getAllCommentsByEventId(Integer eventId, Integer from, Integer size) {
        return CommentMapper.allToCommentDto(commentRepository.findAllByEventId(eventId, PageRequest.of(from, size)));
    }

    @Override
    public CommentDto findById(Integer commentId) {
        validateId(commentId);
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Не найден комментарий с ID = %s", commentId))));
    }

    @Override
    public List<Comment> findAllByParam(String text, Integer[] users, Integer[] events,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        QComment comment = QComment.comment;
        List<BooleanExpression> conditions = new ArrayList<>();

        if (text != null) {
            String textLowerCase = text.toLowerCase();
            conditions.add(comment.text.toLowerCase().like("%" + textLowerCase + "%"));
        }
        if (users != null) {
            conditions.add(comment.author.id.in(users));
        }
        if (events != null) {
            conditions.add(comment.eventId.in(events));
        }
        if (rangeStart != null) {
            conditions.add(comment.created.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(comment.created.before(rangeEnd));
        }
        Optional<BooleanExpression> booleanExpression = conditions.stream().reduce(BooleanExpression::and);
        if (booleanExpression.isEmpty()) {
            return new ArrayList<>();
        }
        Iterable<Comment> comments = commentRepository.findAll(booleanExpression.get(), PageRequest.of(from, size));
        return CommentMapper.mapToComment(comments);
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
