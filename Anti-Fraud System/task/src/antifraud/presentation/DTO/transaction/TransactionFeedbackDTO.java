package antifraud.presentation.DTO.transaction;

import antifraud.business.model.enums.TransactionStatus;

import javax.validation.constraints.NotNull;

public record TransactionFeedbackDTO(long transactionId, @NotNull TransactionStatus feedback) {
}
