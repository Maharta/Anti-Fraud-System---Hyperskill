package antifraud.presentation.DTO.transaction;

import javax.validation.constraints.NotBlank;

public record TransactionDTO(@NotBlank long amount) {
}
