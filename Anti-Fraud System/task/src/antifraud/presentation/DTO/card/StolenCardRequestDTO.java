package antifraud.presentation.DTO.card;

import javax.validation.constraints.NotBlank;

public record StolenCardRequestDTO(@NotBlank String number) {
}
