package com.example.cafenea.controller;

import com.example.cafenea.model.Masa;
import com.example.cafenea.service.MasaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/mese")
@PreAuthorize("hasRole('ADMIN')")
public class MasaController {

    private final MasaService masaService;

    public MasaController(MasaService masaService) {
        this.masaService = masaService;
    }

    @GetMapping
    public String listeazaMese(Model model) {
        model.addAttribute("mese", masaService.getAllMese());
        return "mese";
    }

    @GetMapping("/nou")
    public String formularMasaNoua(Model model) {
        Masa masa = new Masa();
        masa.setStatus("LIBERA");
        model.addAttribute("masa", masa);
        return "formular-masa";
    }

    @GetMapping("/editeaza/{id}")
    public String formularEditare(@PathVariable Long id, Model model) {
        model.addAttribute("masa", masaService.getMasaById(id));
        return "formular-masa";
    }

    @PostMapping("/salveaza")
    public String salveazaMasa(@Valid @ModelAttribute("masa") Masa masa,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "formular-masa";
        }

        try {
            masaService.salveazaMasa(masa);
        } catch (IllegalArgumentException e) {
            model.addAttribute("eroare", e.getMessage());
            return "formular-masa";
        }

        return "redirect:/mese";
    }

    @GetMapping("/sterge/{id}")
    public String stergeMasa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masaService.stergeMasa(id);
            redirectAttributes.addFlashAttribute("success", "Masa a fost stearsa cu succes.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/mese";
    }
}
