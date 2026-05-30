package com.example.cafenea.controller;

import com.example.cafenea.model.CategorieProdus;
import com.example.cafenea.repository.CategorieProdusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorii")
public class CategorieController {

    @Autowired
    private CategorieProdusRepository categorieProdusRepository;

    // Afișează lista și formularul (care poate fi gol pentru adăugare SAU precompletat pentru editare)
    @GetMapping
    public String listeazaCategorii(Model model) {
        model.addAttribute("listaCategorii", categorieProdusRepository.findAll());
        if (!model.containsAttribute("categorieNoua")) {
            model.addAttribute("categorieNoua", new CategorieProdus());
        }
        return "categorii";
    }

    // Salvează o categorie nouă SAU actualizează una existentă (dacă are ID)
    @PostMapping("/salveaza")
    public String salveazaCategorie(@ModelAttribute("categorieNoua") CategorieProdus categorie) {
        if (categorie.getDenumire() != null && !categorie.getDenumire().trim().isEmpty()) {
            categorieProdusRepository.save(categorie);
        }
        return "redirect:/categorii";
    }

    // Ruta GET pentru Editare: Pune categoria selectată în formularul de sus
    @GetMapping("/editeaza/{id}")
    public String pregatesteEditare(@PathVariable Long id, Model model) {
        CategorieProdus catExistenta = categorieProdusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria nu există!"));

        model.addAttribute("listaCategorii", categorieProdusRepository.findAll());
        model.addAttribute("categorieNoua", catExistenta); // Formularul va avea acum datele ei și ID-ul ascuns
        return "categorii";
    }

    // Ruta GET pentru Ștergere
    @GetMapping("/sterge/{id}")
    public String stergeCategorie(@PathVariable Long id) {
        try {
            categorieProdusRepository.deleteById(id);
        } catch (Exception e) {
            // Dacă categoria are produse legate de ea, baza de date va bloca ștergerea (Foreign Key Constraint)
            return "redirect:/categorii?eroare=true";
        }
        return "redirect:/categorii";
    }
}