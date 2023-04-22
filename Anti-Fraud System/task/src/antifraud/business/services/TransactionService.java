package antifraud.business.services;

import antifraud.business.Transaction;
import antifraud.business.model.enums.TransactionStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public TransactionStatus checkFraud(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction Amount is lower than 0!");
        }
        if (transaction.getAmount() > 1500) {
            return TransactionStatus.PROHIBITED;
        }
        if (transaction.getAmount() <= 1500
                && transaction.getAmount() > 200) {
            return TransactionStatus.MANUAL_PROCESSING;
        } else {
            return TransactionStatus.ALLOWED;
        }

    }

}

