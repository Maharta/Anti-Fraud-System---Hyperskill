package antifraud.business.services;

import antifraud.business.model.entity.Card;
import antifraud.persistence.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card getOrCreateCard(String number) {
        return cardRepository.findByNumber(number).orElse(new Card(number));
    }
}
