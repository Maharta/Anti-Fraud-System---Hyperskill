package antifraud.presentation.DTO.transaction;

import antifraud.business.model.enums.Region;
import antifraud.presentation.validation.ValidCardNumber;
import antifraud.presentation.validation.ValidIP;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TransactionRequestDTO(@NotNull long amount,
                                    @ValidIP String ip,
                                    @ValidCardNumber String number,
                                    Region region,
                                    @JsonProperty("date") LocalDateTime dateTime) {
}
