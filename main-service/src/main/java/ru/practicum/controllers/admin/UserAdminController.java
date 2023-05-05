package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.User;
import ru.practicum.services.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public List<User> getListUsers(@RequestParam(required = false) Integer[] ids,
                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return userService.getListUsers(ids, from, size);
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> delete(@PathVariable Integer userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
