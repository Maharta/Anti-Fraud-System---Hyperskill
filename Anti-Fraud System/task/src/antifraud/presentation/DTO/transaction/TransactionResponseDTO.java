package antifraud.presentation.DTO.transaction;

import antifraud.business.model.enums.TransactionReason;
import antifraud.business.model.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TransactionResponseDTO {
    @JsonIgnore
    private final List<TransactionReason> transactionReasons;

    @JsonProperty("result")
    private TransactionStatus status;

    public TransactionResponseDTO() {
        this.transactionReasons = new ArrayList<>();
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus result) {
        this.status = result;
    }

    public void addReason(TransactionReason prohibitedReason) {
        transactionReasons.add(prohibitedReason);
    }

    @JsonProperty("info")
    public String getInfo() {
        if (transactionReasons.isEmpty()) {
            return "none";
        }
        StringBuilder infoBuilder = new StringBuilder();
        Collections.sort(transactionReasons);

        for (int i = 0; i < transactionReasons.size(); i++) {
            TransactionReason prohibitedReason = transactionReasons.get(i);
            String currentInfo = prohibitedReason.name().toLowerCase().replace('_', '-');
            if (i == transactionReasons.size() - 1) {
                infoBuilder.append(currentInfo);
            } else {
                infoBuilder.append(currentInfo).append(", ");
            }
        }
        return infoBuilder.toString();
    }
}
