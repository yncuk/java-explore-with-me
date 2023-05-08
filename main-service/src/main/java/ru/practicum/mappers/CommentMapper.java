package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.model.Comment;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.CommentShortDto;
import ru.practicum.model.dto.NewCommentDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CommentMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getEventId(),
                UserMapper.toUserShortDto(comment.getAuthor()),
                comment.getCreated().format(FORMATTER),
                comment.getStatus()
        );
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return new CommentShortDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated().format(FORMATTER),
                comment.getStatus()
        );
    }

    public static Comment toComment(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEventId(newCommentDto.getEventId());
        return comment;
    }

    public static List<CommentShortDto> allToCommentShortDto(List<Comment> comments) {
        if (comments != null) {
            return comments.stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
        }
        return null;
    }

    public static List<CommentDto> allToCommentDto(List<Comment> comments) {
        if (comments != null) {
            return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        }
        return null;
    }

    public static List<Comment> mapToComment(Iterable<Comment> comments) {
        if (comments != null) {
            return StreamSupport.stream(comments.spliterator(), false).collect(Collectors.toList());
        }
        return null;
    }
}
