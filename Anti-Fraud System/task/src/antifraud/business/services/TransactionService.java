package antifraud.business.services;

import antifraud.business.model.entity.IP;
import antifraud.business.model.entity.StolenCard;
import antifraud.business.model.enums.ProhibitedReason;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.persistence.IPRepository;
import antifraud.persistence.StolenCardRepository;
import antifraud.presentation.DTO.transaction.TransactionRequestDTO;
import antifraud.presentation.DTO.transaction.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TransactionService {

    private final IPRepository ipRepository;
    private final StolenCardRepository stolenCardRepository;

    @Autowired
    public TransactionService(IPRepository ipRepository, StolenCardRepository stolenCardRepository) {
        this.ipRepository = ipRepository;
        this.stolenCardRepository = stolenCardRepository;
    }

    private boolean checkIfIPBlacklisted(String ipAddress) {
        Optional<IP> ip = ipRepository.findByIp(ipAddress);

        return ip.isPresent();
    }

    private boolean checkIfCardBlacklisted(String cardNumber) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(cardNumber);

        return stolenCard.isPresent();
    }

    public TransactionResponseDTO checkFraud(TransactionRequestDTO transaction) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        if (transaction.amount() <= 0) {
            throw new IllegalArgumentException("Transaction amount is 0 or lower than 0!");
        }

        boolean isIPBlacklisted = checkIfIPBlacklisted(transaction.ip());
        boolean isCardBlacklisted = checkIfCardBlacklisted(transaction.number());

        if (transaction.amount() > 1500 || isIPBlacklisted || isCardBlacklisted) {
            transactionResponseDTO.setResult(TransactionStatus.PROHIBITED);
            if (transaction.amount() > 1500) {
                transactionResponseDTO.addProhibitedReason(ProhibitedReason.AMOUNT);
            }
            if (isCardBlacklisted) {
                transactionResponseDTO.addProhibitedReason(ProhibitedReason.CARD_NUMBER);
            }
            if (isIPBlacklisted) {
                transactionResponseDTO.addProhibitedReason(ProhibitedReason.IP);
            }
        } else if (transaction.amount() <= 1500 && transaction.amount() > 200) {
            transactionResponseDTO.setResult(TransactionStatus.MANUAL_PROCESSING);
        } else {
            transactionResponseDTO.setResult(TransactionStatus.ALLOWED);
        }

        return transactionResponseDTO;
    }

}

