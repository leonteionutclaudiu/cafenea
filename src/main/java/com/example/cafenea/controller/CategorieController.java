package com.example.cafenea.controller;

import com.example.cafenea.model.CategorieProdus;
import com.example.cafenea.service.CategorieService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorii")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @GetMapping
    public String listeazaCategorii(Model model) {
        model.addAttribute("listaCategorii", categorieService.getAllCategorii());
        if (!model.containsAttribute("categorieNoua")) {
            model.addAttribute("categorieNoua", new CategorieProdus());
        }
        return "categorii";
    }

    @PostMapping("/salveaza")
    public String salveazaCategorie(@Valid @ModelAttribute("categorieNoua") CategorieProdus categorie,
                                    BindingResult result,
                                    Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listaCategorii", categorieService.getAllCategorii());
            return "categorii";
        }

        categorieService.salveazaCategorie(categorie);
        return "redirect:/categorii";
    }

    @GetMapping("/editeaza/{id}")
    public String pregatesteEditare(@PathVariable Long id, Model model) {
        model.addAttribute("listaCategorii", categorieService.getAllCategorii());
        model.addAttribute("categorieNoua", categorieService.getCategorieById(id));
        return "categorii";
    }

    @GetMapping("/sterge/{id}")
    public String stergeCategorie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categorieService.stergeCategorie(id);
            redirectAttributes.addFlashAttribute("succes", "Categoria a fost stearsa cu succes.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("eroare", e.getMessage());
        }
        return "redirect:/categorii";
    }
}
