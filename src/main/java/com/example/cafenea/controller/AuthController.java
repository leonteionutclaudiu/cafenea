package com.example.cafenea.controller;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- LOGARE ---
    @GetMapping("/login")
    public String arataPaginaLogin(Authentication authentication) {
        // Dacă utilizatorul este deja autentificat, îl trimitem la home sau dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    // --- ÎNREGISTRARE PERSONAL NOU (DOAR ADMIN) ---
    @GetMapping("/register")
    public String arataPaginaRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String proceseazaInregistrare(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String rol,
            RedirectAttributes redirectAttributes) {

        if (utilizatorRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("eroare", "Acest nume de utilizator este deja utilizat!");
            return "redirect:/register";
        }

        Utilizator nouAngajat = new Utilizator();
        nouAngajat.setUsername(username);
        nouAngajat.setPassword(passwordEncoder.encode(password));
        nouAngajat.setRol(rol);

        utilizatorRepository.save(nouAngajat);

        // Redirecționăm la lista de personal cu mesaj de succes
        redirectAttributes.addFlashAttribute("success", "Angajatul " + username + " a fost creat cu succes!");
        return "redirect:/utilizatori";
    }

    // --- SCHIMBARE PAROLĂ (UNIVERSAL: PENTRU ORICE USER LOGAT) ---
    @GetMapping("/schimbare-parola")
    public String arataPaginaSchimbareParola() {
        return "schimbare-parola";
    }

    @PostMapping("/schimbare-parola")
    public String proceseazaSchimbareParola(
            @AuthenticationPrincipal UserDetails userDetails, // Extrage automat utilizatorul curent din sesiune
            @RequestParam String parolaActuala,
            @RequestParam String parolaNoua,
            @RequestParam String confirmaParolaNoua,
            RedirectAttributes redirectAttributes) {

        // Validarea 1: Cele două câmpuri de parolă nouă trebuie să coincidă perfect
        if (!parolaNoua.equals(confirmaParolaNoua)) {
            redirectAttributes.addFlashAttribute("eroare", "Noua parolă și confirmarea ei nu coincid!");
            return "redirect:/schimbare-parola";
        }

        // Căutăm entitatea în baza de date pe baza utilizatorului extras din sesiune
        Utilizator utilizator = utilizatorRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Utilizator negăsit în baza de date."));

        // Validarea 2: Verificăm dacă parola actuală introdusă se potrivește cu hash-ul din DB
        if (!passwordEncoder.matches(parolaActuala, utilizator.getPassword())) {
            redirectAttributes.addFlashAttribute("eroare", "Parola actuală introdusă este incorectă!");
            return "redirect:/schimbare-parola";
        }

        // Dacă validările au trecut, criptăm noua parolă și salvăm modificările
        utilizator.setPassword(passwordEncoder.encode(parolaNoua));
        utilizatorRepository.save(utilizator);

        redirectAttributes.addFlashAttribute("succes", "Parola ta a fost modificată cu succes!");
        return "redirect:/schimbare-parola";
    }
}