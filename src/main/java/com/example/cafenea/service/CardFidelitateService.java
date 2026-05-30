// CardFidelitateService.java
package com.example.cafenea.service;

import com.example.cafenea.model.CardFidelitate;
import com.example.cafenea.repository.CardFidelitateRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CardFidelitateService {
    private final CardFidelitateRepository cardRepository;

    public CardFidelitateService(CardFidelitateRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<CardFidelitate> getAllCards() {
        return cardRepository.findAll();
    }

    public CardFidelitate getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cardul nu a fost gasit."));
    }

    public CardFidelitate saveCard(CardFidelitate card) {
        return cardRepository.save(card);
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}