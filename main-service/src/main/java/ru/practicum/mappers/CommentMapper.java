package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Comment;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.NewCommentDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class CommentMapper {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CommentDto toCommentDto(Comment comment) {
        if (comment.getUpdated() != null) {
            return new CommentDto(
                    comment.getId(),
                    comment.getText(),
                    comment.getEventId(),
                    UserMapper.toUserShortDto(comment.getAuthor()),
                    comment.getCreated().format(FORMATTER),
                    comment.getStatus(),
                    comment.getUpdated().format(FORMATTER)
            );
        } else {
            return new CommentDto(
                    comment.getId(),
                    comment.getText(),
                    comment.getEventId(),
                    UserMapper.toUserShortDto(comment.getAuthor()),
                    comment.getCreated().format(FORMATTER),
                    comment.getStatus(),
                    null
            );
        }
    }

    public Comment toComment(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEventId(newCommentDto.getEventId());
        return comment;
    }

    public List<CommentDto> allToCommentDto(List<Comment> comments) {
        if (comments != null) {
            return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        }
        return null;
    }

    public List<Comment> mapToComment(Iterable<Comment> comments) {
        if (comments != null) {
            return StreamSupport.stream(comments.spliterator(), false).collect(Collectors.toList());
        }
        return null;
    }
}
