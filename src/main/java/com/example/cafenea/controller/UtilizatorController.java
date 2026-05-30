package com.example.cafenea.controller;

import com.example.cafenea.model.CardFidelitate;
import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.repository.CardFidelitateRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@RequestMapping("/utilizatori")
public class UtilizatorController {

    private final UtilizatorRepository utilizatorRepository;
    private final CardFidelitateRepository cardRepository;

    public UtilizatorController(UtilizatorRepository utilizatorRepository, CardFidelitateRepository cardRepository) {
        this.utilizatorRepository = utilizatorRepository;
        this.cardRepository = cardRepository;
    }

    // READ: Afișăm toți utilizatorii și cardurile lor
    @GetMapping
    public String listeazaUtilizatori(Model model) {
        model.addAttribute("utilizatori", utilizatorRepository.findAll());
        return "lista-utilizatori";
    }

    // CREATE (Card): Generăm automat un card atașat utilizatorului
    @GetMapping("/genereaza-card/{userId}")
    public String genereazaCard(@PathVariable Long userId) {
        Utilizator user = utilizatorRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu există"));

        if (user.getCardFidelitate() == null) {
            CardFidelitate card = new CardFidelitate();
            card.setCodCard("CAFE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            card.setPuncteAcumulate(10); // Cadou de bun venit!
            card.setUtilizator(user);
            cardRepository.save(card);
        }
        return "redirect:/utilizatori";
    }

    // DELETE (Card): Ștergem cardul de fidelitate
    @GetMapping("/sterge-card/{cardId}")
    public String stergeCard(@PathVariable Long cardId) {
        cardRepository.deleteById(cardId);
        return "redirect:/utilizatori";
    }
}