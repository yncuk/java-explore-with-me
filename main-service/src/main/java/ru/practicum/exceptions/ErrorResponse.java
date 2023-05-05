package ru.practicum.exceptions;

import lombok.Value;

@Value
public class ErrorResponse {
    String status;
    String reason;
    String message;
    String timestamp;
}
