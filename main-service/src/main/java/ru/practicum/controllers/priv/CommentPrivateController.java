package ru.practicum.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.*;
import ru.practicum.services.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentDto findByUserIdAndCommentId(@PathVariable Integer userId,
                                               @PathVariable Integer commentId) {
        return commentService.findByUserIdAndCommentId(userId, commentId);
    }

    @PostMapping("/{userId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Integer userId,
                                             @RequestBody @Valid NewCommentDto newCommentDto) {
        return new ResponseEntity<>(commentService.create(userId, newCommentDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto update(@PathVariable Integer userId,
                             @PathVariable Integer commentId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.update(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Integer userId,
                                       @PathVariable Integer commentId) {
        commentService.delete(userId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
