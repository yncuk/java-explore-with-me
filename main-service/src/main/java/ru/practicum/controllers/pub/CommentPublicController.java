package ru.practicum.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.services.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllComments(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllComments(from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto findById(@PathVariable Integer commentId) {
        return commentService.findById(commentId);
    }
}
