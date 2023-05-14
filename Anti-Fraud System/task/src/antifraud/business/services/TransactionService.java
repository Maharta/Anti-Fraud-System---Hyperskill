package antifraud.business.services;

import antifraud.business.model.entity.IP;
import antifraud.business.model.entity.StolenCard;
import antifraud.business.model.entity.Transaction;
import antifraud.business.model.enums.Region;
import antifraud.business.model.enums.TransactionReason;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.persistence.IPRepository;
import antifraud.persistence.StolenCardRepository;
import antifraud.persistence.TransactionRepository;
import antifraud.presentation.DTO.transaction.TransactionRequestDTO;
import antifraud.presentation.DTO.transaction.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TransactionService {

    private final IPRepository ipRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(IPRepository ipRepository,
                              StolenCardRepository stolenCardRepository,
                              TransactionRepository transactionRepository) {
        this.ipRepository = ipRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponseDTO beginTransaction(TransactionRequestDTO transactionRequest) {
        if (transactionRequest.amount() <= 0) {
            throw new IllegalArgumentException("Transaction amount is 0 or lower than 0!");
        }

        TransactionResponseDTO transactionResponseDTO = getTransactionResponse(transactionRequest);

        Transaction transaction = new Transaction(transactionRequest.amount(),
                transactionRequest.ip(),
                transactionRequest.number(),
                transactionRequest.region(),
                transactionRequest.dateTime(),
                transactionResponseDTO.getStatus());

        transactionRepository.save(transaction);


        return transactionResponseDTO;
    }

    private TransactionResponseDTO getTransactionResponse(TransactionRequestDTO transactionRequest) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        boolean isIPBlacklisted = checkIfIPBlacklisted(transactionRequest.ip());
        boolean isCardBlacklisted = checkIfCardBlacklisted(transactionRequest.number());


        List<Transaction> lastHourTransactionList = transactionRepository.findAllTransactionByNumberBetweenDatetime(
                transactionRequest.dateTime().minusHours(1),
                transactionRequest.dateTime(),
                transactionRequest.number()
        );

        Set<String> sameIpSet = new HashSet<>();
        List<Transaction> distinctIPTransaction = lastHourTransactionList.stream().filter(e -> sameIpSet.add(e.getIp())).toList();

        Set<Region> sameRegionSet = new HashSet<>();
        List<Transaction> distinctRegionTransaction = lastHourTransactionList.stream().filter(e -> sameRegionSet.add(e.getRegion())).toList();

        int distinctIPTransactionCount = 0;
        int distinctRegionTransactionCount = 0;

        for (Transaction transaction : distinctIPTransaction) {
            if (!Objects.equals(transaction.getIp(), transactionRequest.ip())) {
                distinctIPTransactionCount++;
            }
        }
        for (Transaction transaction : distinctRegionTransaction) {
            if (!Objects.equals(transaction.getRegion(), transactionRequest.region())) {
                distinctRegionTransactionCount++;
            }
        }
        System.out.println(distinctIPTransactionCount);
        System.out.println(distinctRegionTransactionCount);

        if (isIPBlacklisted || isCardBlacklisted || transactionRequest.amount() > 1500
                || distinctIPTransactionCount > 2 || distinctRegionTransactionCount > 2) {
            transactionResponseDTO.setStatus(TransactionStatus.PROHIBITED);
        } else if (transactionRequest.amount() > 200 || distinctIPTransactionCount == 2
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

    private boolean checkIfIPBlacklisted(String ipAddress) {
        Optional<IP> ip = ipRepository.findByIp(ipAddress);

        return ip.isPresent();
    }

    private boolean checkIfCardBlacklisted(String cardNumber) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(cardNumber);

        return stolenCard.isPresent();
    }


}

