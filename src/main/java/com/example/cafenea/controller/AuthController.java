package com.example.cafenea.controller;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.service.UtilizatorService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UtilizatorService utilizatorService;

    public AuthController(UtilizatorService utilizatorService) {
        this.utilizatorService = utilizatorService;
    }

    @GetMapping("/login")
    public String arataPaginaLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String arataPaginaRegister(Model model) {
        if (!model.containsAttribute("utilizator")) {
            model.addAttribute("utilizator", new Utilizator());
        }
        return "register";
    }

    @PostMapping("/register")
    public String proceseazaInregistrare(
            @Valid @ModelAttribute("utilizator") Utilizator utilizator,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            utilizatorService.inregistreazaUtilizator(utilizator);
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "username.duplicat", "Acest nume de utilizator este deja utilizat!");
            return "register";
        }

        redirectAttributes.addFlashAttribute("success", "Angajatul " + utilizator.getUsername() + " a fost creat cu succes!");
        return "redirect:/utilizatori";
    }

    @GetMapping("/schimbare-parola")
    public String arataPaginaSchimbareParola() {
        return "schimbare-parola";
    }

    @PostMapping("/schimbare-parola")
    public String proceseazaSchimbareParola(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String parolaActuala,
            @RequestParam String parolaNoua,
            @RequestParam String confirmaParolaNoua,
            RedirectAttributes redirectAttributes) {

        try {
            utilizatorService.schimbaParola(userDetails.getUsername(), parolaActuala, parolaNoua, confirmaParolaNoua);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("eroare", e.getMessage());
            return "redirect:/schimbare-parola";
        }

        redirectAttributes.addFlashAttribute("succes", "Parola ta a fost modificata cu succes!");
        return "redirect:/schimbare-parola";
    }
}
