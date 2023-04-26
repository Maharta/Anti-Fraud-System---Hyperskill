package antifraud.presentation.DTO.transaction;

import antifraud.business.model.enums.ProhibitedReason;
import antifraud.business.model.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TransactionResponseDTO {
    @JsonIgnore
    private final List<ProhibitedReason> prohibitedReasons;
    
    private TransactionStatus result;

    public TransactionResponseDTO() {
        this.prohibitedReasons = new ArrayList<>();
    }

    public TransactionStatus getResult() {
        return result;
    }

    public void setResult(TransactionStatus result) {
        this.result = result;
    }

    public void addProhibitedReason(ProhibitedReason prohibitedReason) {
        prohibitedReasons.add(prohibitedReason);
    }

    @JsonProperty("info")
    public String getInfo() {
        if (prohibitedReasons.isEmpty()) {
            return "none";
        }

        StringBuilder infoBuilder = new StringBuilder();
        for (int i = 0; i < prohibitedReasons.size(); i++) {
            ProhibitedReason prohibitedReason = prohibitedReasons.get(i);
            String currentInfo;
            if (prohibitedReason.equals(ProhibitedReason.CARD_NUMBER)) {
                currentInfo = "card-number";
            } else {
                currentInfo = prohibitedReason.name().toLowerCase();
            }

            if (i == prohibitedReasons.size() - 1) {
                infoBuilder.append(currentInfo);
            } else {
                infoBuilder.append(currentInfo).append(", ");
            }
        }
        return infoBuilder.toString();
    }
}
