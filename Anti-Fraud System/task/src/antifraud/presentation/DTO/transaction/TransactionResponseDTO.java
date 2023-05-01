package antifraud.presentation.DTO.transaction;

import antifraud.business.model.enums.TransactionReason;
import antifraud.business.model.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TransactionResponseDTO {
    @JsonIgnore
    private final List<TransactionReason> transactionReasons;

    private TransactionStatus result;

    public TransactionResponseDTO() {
        this.transactionReasons = new ArrayList<>();
    }

    public TransactionStatus getResult() {
        return result;
    }

    public void setResult(TransactionStatus result) {
        this.result = result;
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
        for (int i = 0; i < transactionReasons.size(); i++) {
            TransactionReason prohibitedReason = transactionReasons.get(i);
            String currentInfo;
            if (prohibitedReason.equals(TransactionReason.CARD_NUMBER)) {
                currentInfo = "card-number";
            } else {
                currentInfo = prohibitedReason.name().toLowerCase();
            }

            if (i == transactionReasons.size() - 1) {
                infoBuilder.append(currentInfo);
            } else {
                infoBuilder.append(currentInfo).append(", ");
            }
        }
        return infoBuilder.toString();
    }
}
