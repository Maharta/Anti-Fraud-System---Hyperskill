package antifraud.presentation.DTO.error;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime dateTime, int errorCode, String message) {}
