package antifraud.business.services;

import antifraud.business.exception.EntityNotFoundException;
import antifraud.business.exception.FeedbackConflictException;
import antifraud.business.exception.UnprocessableEntityException;
import antifraud.business.model.entity.Card;
import antifraud.business.model.entity.Transaction;
import antifraud.business.model.enums.Region;
import antifraud.business.model.enums.TransactionReason;
import antifraud.business.model.enums.TransactionStatus;
import antifraud.persistence.TransactionRepository;
import antifraud.presentation.DTO.transaction.TransactionDTO;
import antifraud.presentation.DTO.transaction.TransactionFeedbackDTO;
import antifraud.presentation.DTO.transaction.TransactionRequestDTO;
import antifraud.presentation.DTO.transaction.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


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


    public List<TransactionDTO> getTransactionHistory() {
        Iterable<Transaction> transactions = transactionRepository.findAll();
        List<TransactionDTO> transactionHistory = new ArrayList<>();
        transactions.forEach(transaction -> transactionHistory.add(transaction.toDTO()));
        return transactionHistory;
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


    public TransactionDTO updateTransactionWithFeedback(TransactionFeedbackDTO transactionFeedbackDTO) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionFeedbackDTO.transactionId());

        if (optionalTransaction.isEmpty()) {
            throw new EntityNotFoundException("Transaction with id " + transactionFeedbackDTO.transactionId() + " could not be found");
        }

        Transaction transaction = optionalTransaction.get();

        System.out.println(transaction);

        if (transaction.getFeedback() != null) {
            throw new FeedbackConflictException(String.format("Transaction with id %d already has a feedback.", transactionFeedbackDTO.transactionId()));
        }

        if (transactionFeedbackDTO.feedback() == transaction.getStatus()) {
            throw new UnprocessableEntityException(String.format("Transaction with id %d already have a status of %s",
                    transactionFeedbackDTO.transactionId(), transactionFeedbackDTO.feedback()));
        }

        Card transactionCard = transaction.getCard();

        switch (transaction.getStatus()) {
            case ALLOWED -> {
                if (transactionFeedbackDTO.feedback() == TransactionStatus.MANUAL_PROCESSING) {
                    transactionCard.setMaxAllowed(decreaseLimit(transactionCard.getMaxAllowed(), transaction.getAmount()));
                }

                if (transactionFeedbackDTO.feedback() == TransactionStatus.PROHIBITED) {
                    transactionCard.setMaxAllowed(decreaseLimit(transactionCard.getMaxAllowed(), transaction.getAmount()));
                    transactionCard.setMaxManual(decreaseLimit(transactionCard.getMaxManual(), transaction.getAmount()));
                }
            }
            case MANUAL_PROCESSING -> {
                if (transactionFeedbackDTO.feedback() == TransactionStatus.ALLOWED) {
                    transactionCard.setMaxAllowed(increaseLimit(transactionCard.getMaxAllowed(), transaction.getAmount()));
                }

                if (transactionFeedbackDTO.feedback() == TransactionStatus.PROHIBITED) {
                    transactionCard.setMaxManual(decreaseLimit(transactionCard.getMaxManual(), transaction.getAmount()));
                }
            }
            case PROHIBITED -> {
                if (transactionFeedbackDTO.feedback() == TransactionStatus.ALLOWED) {
                    transactionCard.setMaxAllowed(increaseLimit(transactionCard.getMaxAllowed(), transaction.getAmount()));
                    transactionCard.setMaxManual(increaseLimit(transactionCard.getMaxManual(), transaction.getAmount()));
                }

                if (transactionFeedbackDTO.feedback() == TransactionStatus.MANUAL_PROCESSING) {
                    transactionCard.setMaxManual(increaseLimit(transactionCard.getMaxManual(), transaction.getAmount()));
                }
            }
        }

        transaction.setFeedback(transactionFeedbackDTO.feedback());

        transactionRepository.save(transaction);

        return transaction.toDTO();
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
                distinctIPTransactionCount, distinctRegionTransactionCount, card);

        return transactionResponseDTO;
    }

    private void addTransactionReason(
            TransactionResponseDTO transactionResponseDTO, long amount, boolean isIPBlacklisted,
            boolean isCardBlacklisted, int distinctIPTransactionCount, int distinctRegionTransactionCount,
            Card card) {

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

            if (amount > card.getMaxManual()) {
                transactionResponseDTO.addReason(TransactionReason.AMOUNT);
            }


        } else {
            if (amount > card.getMaxAllowed()) {
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


    private int increaseLimit(int currentLimit, int transactionValue) {
        return (int) Math.ceil(0.8 * currentLimit + 0.2 * transactionValue);
    }

    private int decreaseLimit(int currentLimit, int transactionValue) {
        return (int) Math.ceil(0.8 * currentLimit - 0.2 * transactionValue);
    }
}

