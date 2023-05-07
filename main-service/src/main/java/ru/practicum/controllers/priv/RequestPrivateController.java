package ru.practicum.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Request;
import ru.practicum.services.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public List<Request> findAllByUserId(@PathVariable Integer userId) {
        return requestService.findAllByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Request> create(@PathVariable Integer userId,
                                          @RequestParam Integer eventId) {
        return new ResponseEntity<>(requestService.create(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public Request delete(@PathVariable Integer userId,
                          @PathVariable Integer requestId) {
        return requestService.delete(userId, requestId);
    }

}
