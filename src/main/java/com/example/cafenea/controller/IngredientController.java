// IngredientController.java
package com.example.cafenea.controller;

import com.example.cafenea.model.Ingredient;
import com.example.cafenea.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
@Controller
@RequestMapping("/ingrediente")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String listaIngrediente(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "numeIngredient") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Page<Ingredient> pageIngredients = ingredientService.getIngredientePaginate(keyword, page, size, sortField, sortDir);

        model.addAttribute("ingrediente", pageIngredients.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageIngredients.getTotalPages());
        model.addAttribute("totalItems", pageIngredients.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        return "lista-ingrediente";
    }

    @GetMapping("/nou")
    public String formularIngredientNou(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "formular-ingredient";
    }

    @PostMapping("/salveaza")
    public String salveazaIngredient(@Valid @ModelAttribute("ingredient") Ingredient ingredient,
                                     BindingResult result) {
        if (result.hasErrors()) {
            return "formular-ingredient";
        }

        ingredientService.saveIngredient(ingredient);
        return "redirect:/ingrediente";
    }

    @GetMapping("/editeaza/{id}")
    public String formularEditare(@PathVariable Long id, Model model) {
        model.addAttribute("ingredient", ingredientService.getIngredientById(id));
        return "formular-ingredient";
    }

    @GetMapping("/sterge/{id}")
    public String stergeIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/ingrediente";
    }
}
