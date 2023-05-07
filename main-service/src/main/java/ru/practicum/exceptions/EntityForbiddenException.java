package ru.practicum.exceptions;

public class EntityForbiddenException extends RuntimeException {
    public EntityForbiddenException(final String message) {
        super(message);
    }
}
