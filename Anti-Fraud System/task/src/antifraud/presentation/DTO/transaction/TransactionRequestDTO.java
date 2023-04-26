package antifraud.presentation.DTO.transaction;

import antifraud.presentation.validation.ValidCardNumber;
import antifraud.presentation.validation.ValidIP;

import javax.validation.constraints.NotNull;

public record TransactionRequestDTO(@NotNull long amount, @ValidIP String ip, @ValidCardNumber String number) {
}
