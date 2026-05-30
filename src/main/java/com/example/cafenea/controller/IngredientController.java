// IngredientController.java
package com.example.cafenea.controller;

import com.example.cafenea.model.Ingredient;
import com.example.cafenea.service.IngredientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ingrediente")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String listaIngrediente(Model model) {
        model.addAttribute("ingrediente", ingredientService.getAllIngredients());
        return "lista-ingrediente";
    }

    @GetMapping("/nou")
    public String formularIngredientNou(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "formular-ingredient";
    }

    @PostMapping("/salveaza")
    public String salveazaIngredient(@ModelAttribute("ingredient") Ingredient ingredient) {
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