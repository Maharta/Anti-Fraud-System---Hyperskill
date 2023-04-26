package antifraud.business.services;

import antifraud.business.exception.EntityNotFoundException;
import antifraud.business.model.entity.StolenCard;
import antifraud.persistence.StolenCardRepository;
import antifraud.presentation.DTO.card.StolenCardRequestDTO;
import antifraud.presentation.DTO.card.StolenCardResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final StolenCardRepository stolenCardRepository;

    @Autowired
    public CardService(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    public StolenCard saveCardAsStolen(StolenCardRequestDTO stolenCardRequestDTO) {
        Optional<StolenCard> cardInDB = stolenCardRepository.findByNumber(stolenCardRequestDTO.number());

        if (cardInDB.isPresent()) {
            throw new EntityExistsException("Card with number %s already exists!".formatted(stolenCardRequestDTO.number()));
        }

        return stolenCardRepository.save(new StolenCard(stolenCardRequestDTO.number()));
    }

    public void deleteCardByNumber(String cardNumber) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findByNumber(cardNumber);

        if (stolenCard.isEmpty()) {
            throw new EntityNotFoundException("Card with number %s does not exists!".formatted(cardNumber));
        }

        stolenCardRepository.delete(stolenCard.get());
    }


    public List<StolenCardResponseDTO> getAllStolenCards() {
        Iterable<StolenCard> stolenCards = stolenCardRepository.findAll();

        List<StolenCardResponseDTO> stolenCardResponseDTOList = new ArrayList<>();
        for (StolenCard stolenCard : stolenCards) {
            stolenCardResponseDTOList.add(new StolenCardResponseDTO(stolenCard.getId(), stolenCard.getNumber()));
        }
        
        return stolenCardResponseDTOList;

    }
}
