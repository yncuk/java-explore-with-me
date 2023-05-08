package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Comment;
import ru.practicum.services.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    public List<Comment> findAllByParam(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) Integer[] users,
                                        @RequestParam(required = false) Integer[] events,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.findAllByParam(text, users, events, rangeStart, rangeEnd, from, size);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable Integer commentId) {
        commentService.deleteById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
