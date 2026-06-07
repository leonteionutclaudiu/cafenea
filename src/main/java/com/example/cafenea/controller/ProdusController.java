package com.example.cafenea.controller;

import com.example.cafenea.model.Produs;
import com.example.cafenea.service.CategorieService;
import com.example.cafenea.service.IngredientService;
import com.example.cafenea.service.ProdusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProdusController {

    @Autowired
    private ProdusService produsService;

    @Autowired
    private CategorieService categorieService;

    @Autowired
    private IngredientService ingredientService;

    @GetMapping("/produse")
    public String listeazaProduse(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categorieId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nume") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Page<Produs> pageProduse = produsService.getProdusePaginate(keyword, categorieId, page, size, sortField, sortDir);

        model.addAttribute("listaProduse", pageProduse.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProduse.getTotalPages());
        model.addAttribute("totalItems", pageProduse.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("categorieSelectata", categorieId);

        model.addAttribute("categorii", categorieService.getAllCategorii());
        model.addAttribute("ingrediente", ingredientService.getAllIngredients());

        return "produse";
    }

    @GetMapping("/produse/nou")
    public String formularProdusNou(Model model) {
        model.addAttribute("produs", new Produs());
        model.addAttribute("categorii", categorieService.getAllCategorii());
        model.addAttribute("ingrediente", ingredientService.getAllIngredients());
        return "formular-produs";
    }

    @GetMapping("/produse/editeaza/{id}")
    public String formularEditareProdus(@PathVariable Long id, Model model) {
        Produs produsExistent = produsService.getProdusDupaId(id);

        model.addAttribute("produs", produsExistent);
        model.addAttribute("categorii", categorieService.getAllCategorii());
        model.addAttribute("ingrediente", ingredientService.getAllIngredients());
        return "formular-produs";
    }

    @PostMapping("/produse/salveaza")
    public String salveazaProdus(@Valid @ModelAttribute("produs") Produs produs, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorii", categorieService.getAllCategorii());
            model.addAttribute("ingrediente", ingredientService.getAllIngredients());
            return "formular-produs";
        }
        produsService.salveazaProdus(produs);
        return "redirect:/produse";
    }

    @GetMapping("/produse/sterge/{id}")
    public String stergeProdus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produsService.stergeProdus(id);
            redirectAttributes.addFlashAttribute("success", "Produsul a fost sters cu succes.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/produse";
    }
}
