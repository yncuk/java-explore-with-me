package ru.practicum.services;

import ru.practicum.model.Comment;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto create(Integer userId, NewCommentDto newCommentDto);

    CommentDto findByUserIdAndCommentId(Integer userId, Integer commentId);

    CommentDto update(Integer userId, Integer commentId, NewCommentDto newCommentDto);

    void delete(Integer userId, Integer commentId);

    List<CommentDto> getAllCommentsByEventId(Integer eventId, Integer from, Integer size);

    CommentDto findById(Integer commentId);

    List<Comment> findAllByParam(String text, Integer[] users, Integer[] events, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    void deleteById(Integer commentId);
}
