package com.example.cafenea.controller;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        if (utilizatorRepository.findByUsername(utilizator.getUsername()).isPresent()) {
            result.rejectValue("username", "username.duplicat", "Acest nume de utilizator este deja utilizat!");
            return "register";
        }

        utilizator.setPassword(passwordEncoder.encode(utilizator.getPassword()));
        utilizatorRepository.save(utilizator);

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

        if (parolaNoua == null || parolaNoua.length() < 6) {
            redirectAttributes.addFlashAttribute("eroare", "Noua parola trebuie sa aiba minimum 6 caractere!");
            return "redirect:/schimbare-parola";
        }

        if (!parolaNoua.equals(confirmaParolaNoua)) {
            redirectAttributes.addFlashAttribute("eroare", "Noua parola si confirmarea ei nu coincid!");
            return "redirect:/schimbare-parola";
        }

        Utilizator utilizator = utilizatorRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Utilizator negasit in baza de date."));

        if (!passwordEncoder.matches(parolaActuala, utilizator.getPassword())) {
            redirectAttributes.addFlashAttribute("eroare", "Parola actuala introdusa este incorecta!");
            return "redirect:/schimbare-parola";
        }

        utilizator.setPassword(passwordEncoder.encode(parolaNoua));
        utilizatorRepository.save(utilizator);

        redirectAttributes.addFlashAttribute("succes", "Parola ta a fost modificata cu succes!");
        return "redirect:/schimbare-parola";
    }
}
