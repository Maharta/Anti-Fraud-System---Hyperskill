package antifraud.presentation.DTO.transaction;

import antifraud.business.model.enums.Region;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.presentation.jackson.serializers.NullAsEmptyStringSerializer;
import antifraud.presentation.validation.ValidCardNumber;
import antifraud.presentation.validation.ValidIP;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public record TransactionDTO(long transactionId, int amount, @ValidIP String ip, @ValidCardNumber String number,
                             Region region, LocalDateTime date, TransactionStatus result,
                             @JsonSerialize(nullsUsing = NullAsEmptyStringSerializer.class) TransactionStatus feedback) {

}
