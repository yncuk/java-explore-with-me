package ru.practicum.exceptions;

public class EntityBadRequestException extends RuntimeException {
    public EntityBadRequestException(final String message) {
        super(message);
    }
}
