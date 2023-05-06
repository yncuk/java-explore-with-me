package ru.practicum.exceptions;

import lombok.Value;

@Value
public class ErrorResponse {
    String status;
    String reason;
    Throwable message;
    String timestamp;
}
