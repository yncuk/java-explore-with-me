package ru.practicum.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.EntityBadRequestException;
import ru.practicum.exceptions.EntityForbiddenException;
import ru.practicum.exceptions.ErrorResponse;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final EntityBadRequestException e) {
        log.debug("Ошибка 400, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(
                "BAD_REQUEST",
                e.getMessage(),
                e.getStackTrace()[0].toString(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotFoundException e) {
        log.debug("Ошибка 404, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(
                "NOT_FOUND",
                e.getMessage(),
                e.getStackTrace()[0].toString(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final DataIntegrityViolationException e) {
        log.debug("Ошибка 409, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(
                "CONFLICT",
                e.getMessage(),
                e.getStackTrace()[0].toString(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbidden(final EntityForbiddenException e) {
        log.debug("Ошибка 409, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(
                "FORBIDDEN",
                e.getMessage(),
                e.getStackTrace()[0].toString(),
                LocalDateTime.now().format(FORMATTER));
    }
}
