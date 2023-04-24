package antifraud.presentation.DTO.card;

import antifraud.presentation.validation.ValidCardNumber;

public record StolenCardRequestDTO(@ValidCardNumber String number) {
}
