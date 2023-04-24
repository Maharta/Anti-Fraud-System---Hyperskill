package antifraud.business.services;

import antifraud.business.exception.InvalidCardNumberException;
import antifraud.business.model.entity.StolenCard;
import antifraud.business.security.validation.Luhn;
import antifraud.persistence.StolenCardRepository;
import antifraud.presentation.DTO.card.StolenCardRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.Optional;

@Service
public class CardService {
    private final StolenCardRepository stolenCardRepository;

    @Autowired
    public CardService(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    public StolenCard saveCardAsStolen(StolenCardRequestDTO stolenCardRequestDTO) {
        boolean isCardNumberValid = checkCardNumberValidity(stolenCardRequestDTO.number());

        if (!isCardNumberValid) {
            throw new InvalidCardNumberException("%s is not a valid card number!".formatted(stolenCardRequestDTO.number()));
        }

        Optional<StolenCard> cardInDB = stolenCardRepository.findByNumber(stolenCardRequestDTO.number());
        
        if (cardInDB.isPresent()) {
            throw new EntityExistsException("Card with number %s already exists!".formatted(stolenCardRequestDTO.number()));
        }

        return stolenCardRepository.save(new StolenCard(stolenCardRequestDTO.number()));
    }

    private boolean checkCardNumberValidity(String number) {
        if (number.length() != 16) {
            return false;
        }

        String numberWithoutChecksum = number.substring(0, number.length() - 1);
        char currentChecksum = number.charAt(number.length() - 1);
        char validChecksum = Luhn.generateValidChecksum(numberWithoutChecksum).charAt(0);

        return currentChecksum == validChecksum;
    }
}
