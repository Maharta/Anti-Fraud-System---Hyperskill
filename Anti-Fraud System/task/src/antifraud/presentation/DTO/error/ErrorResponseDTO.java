package antifraud.presentation.DTO.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(LocalDateTime dateTime, int statusCode, String message) {
}
