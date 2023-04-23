package antifraud.business.services;

import antifraud.business.model.enums.TransactionStatus;
import antifraud.presentation.DTO.transaction.TransactionDTO;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    public TransactionStatus checkFraud(TransactionDTO transaction) {
        if (transaction.amount() <= 0) {
            throw new IllegalArgumentException("Transaction Amount is lower than 0!");
        }
        if (transaction.amount() > 1500) {
            return TransactionStatus.PROHIBITED;
        }
        if (transaction.amount() <= 1500
                && transaction.amount() > 200) {
            return TransactionStatus.MANUAL_PROCESSING;
        } else {
            return TransactionStatus.ALLOWED;
        }

    }

}

