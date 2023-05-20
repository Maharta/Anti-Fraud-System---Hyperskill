package antifraud.business.services;

import antifraud.business.model.entity.Card;
import antifraud.business.model.entity.Transaction;
import antifraud.business.model.enums.Region;
import antifraud.business.model.enums.TransactionReason;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.persistence.TransactionRepository;
import antifraud.presentation.DTO.transaction.TransactionDTO;
import antifraud.presentation.DTO.transaction.TransactionRequestDTO;
import antifraud.presentation.DTO.transaction.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class TransactionService {

    private final CardService cardService;
    private final StolenCardService stolenCardService;
    private final IPService ipService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(CardService cardService,
                              IPService ipService,
                              StolenCardService stolenCardService,
                              TransactionRepository transactionRepository) {
        this.cardService = cardService;
        this.stolenCardService = stolenCardService;
        this.ipService = ipService;
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getNumberTransactionHistory(String number) {
        List<Transaction> transactions = transactionRepository.findAllTransactionByNumber(number);
        return transactions.stream().map(Transaction::toDTO).toList();
    }

    public TransactionResponseDTO beginTransaction(TransactionRequestDTO transactionRequest) {
        if (transactionRequest.amount() <= 0) {
            throw new IllegalArgumentException("Transaction amount is 0 or lower than 0!");
        }

        Card card = cardService.getOrCreateCard(transactionRequest.number());

        TransactionResponseDTO transactionResponseDTO = getTransactionResponse(transactionRequest, card);

        Transaction transaction = new Transaction(transactionRequest.amount(),
                transactionRequest.ip(),
                transactionRequest.region(),
                transactionRequest.dateTime(),
                transactionResponseDTO.getStatus(),
                card);

        transactionRepository.save(transaction);


        return transactionResponseDTO;
    }

    private TransactionResponseDTO getTransactionResponse(TransactionRequestDTO transactionRequest, Card card) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        boolean isIPBlacklisted = ipService.isIPBlacklisted(transactionRequest.ip());
        boolean isCardBlacklisted = stolenCardService.isCardBlacklisted(transactionRequest.number());

        List<Transaction.RegionAndIP> lastHourRegionAndIp = transactionRepository.
                findAllDistinctRegionAndIPTransactionBetweenDateTime(
                        transactionRequest.dateTime().minusHours(1),
                        transactionRequest.dateTime(),
                        transactionRequest.number()
                );

        Set<Region> regionSet = new HashSet<>();
        Set<String> ipSet = new HashSet<>();

        lastHourRegionAndIp.forEach((regionAndIP -> {
            regionSet.add(regionAndIP.getRegion());
            ipSet.add(regionAndIP.getIp());
        }));

        regionSet.remove(transactionRequest.region());
        ipSet.remove(transactionRequest.ip());

        int distinctRegionTransactionCount = regionSet.size();
        int distinctIPTransactionCount = ipSet.size();

        if (isIPBlacklisted || isCardBlacklisted || transactionRequest.amount() > card.getMaxManual()
                || distinctIPTransactionCount > 2 || distinctRegionTransactionCount > 2) {
            transactionResponseDTO.setStatus(TransactionStatus.PROHIBITED);
        } else if (transactionRequest.amount() > card.getMaxAllowed() || distinctIPTransactionCount == 2
                || distinctRegionTransactionCount == 2) {
            transactionResponseDTO.setStatus(TransactionStatus.MANUAL_PROCESSING);
        } else {
            transactionResponseDTO.setStatus(TransactionStatus.ALLOWED);
        }

        addTransactionReason(transactionResponseDTO, transactionRequest.amount(),
                isIPBlacklisted, isCardBlacklisted,
                distinctIPTransactionCount, distinctRegionTransactionCount);

        return transactionResponseDTO;
    }

    private void addTransactionReason(
            TransactionResponseDTO transactionResponseDTO, long amount, boolean isIPBlacklisted,
            boolean isCardBlacklisted, int distinctIPTransactionCount, int distinctRegionTransactionCount
    ) {

        if (transactionResponseDTO.getStatus() == TransactionStatus.ALLOWED) {
            return;
        }

        if (transactionResponseDTO.getStatus() == TransactionStatus.PROHIBITED) {
            if (isIPBlacklisted) {
                transactionResponseDTO.addReason(TransactionReason.IP);
            }

            if (isCardBlacklisted) {
                transactionResponseDTO.addReason(TransactionReason.CARD_NUMBER);
            }

            if (distinctIPTransactionCount > 2) {
                transactionResponseDTO.addReason(TransactionReason.IP_CORRELATION);
            }

            if (distinctRegionTransactionCount > 2) {
                transactionResponseDTO.addReason(TransactionReason.REGION_CORRELATION);
            }

            if (amount > 1500) {
                transactionResponseDTO.addReason(TransactionReason.AMOUNT);
            }


        } else {
            if (amount > 200) {
                transactionResponseDTO.addReason(TransactionReason.AMOUNT);
            }

            if (distinctIPTransactionCount == 2) {
                transactionResponseDTO.addReason(TransactionReason.IP_CORRELATION);
            }

            if (distinctRegionTransactionCount == 2) {
                transactionResponseDTO.addReason(TransactionReason.REGION_CORRELATION);
            }
        }


    }


}

