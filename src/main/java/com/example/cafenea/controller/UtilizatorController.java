package com.example.cafenea.controller;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.service.UtilizatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import org.springframework.security.access.AccessDeniedException;

@Controller
@RequestMapping("/utilizatori")
public class UtilizatorController {

    private final UtilizatorService utilizatorService;
    private final UtilizatorRepository utilizatorRepository;

    public UtilizatorController(UtilizatorService utilizatorService, UtilizatorRepository utilizatorRepository) {
        this.utilizatorService = utilizatorService;
        this.utilizatorRepository = utilizatorRepository;
    }

    @GetMapping
    public String listeazaUtilizatori(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Page<Utilizator> pageUtilizatori = utilizatorService.getUtilizatoriPaginati(keyword, page, size, sortField, sortDir);

        model.addAttribute("utilizatori", pageUtilizatori.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageUtilizatori.getTotalPages());
        model.addAttribute("totalItems", pageUtilizatori.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        return "lista-utilizatori";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/sterge/{id}")
    public String stergeUtilizator(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        Utilizator userToDelete = utilizatorRepository.findById(id).orElse(null);

        if (userToDelete == null) {
            redirectAttributes.addFlashAttribute("error", "Utilizatorul nu există!");
            return "redirect:/utilizatori";
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isCurrentUser = userToDelete.getUsername().equals(currentUsername);

        try {
            utilizatorRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Utilizator șters cu succes!");

            if (isCurrentUser) {
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                return "redirect:/login?logout";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare: Utilizatorul are comenzi asociate!");
        }

        return "redirect:/utilizatori";
    }

    @PostMapping("/salveaza-profil")
    public String salveazaProfil(
            @Valid @ModelAttribute("profil") com.example.cafenea.model.ProfilUtilizator profil,
            BindingResult result, // BindingResult trebuie să stea imediat după @Valid
            @RequestParam("utilizatorId") Long utilizatorId,
            Model model, // Adăugăm Model pentru a putea retrimite obiectele în pagină
            RedirectAttributes redirectAttributes) {

        // 1. Verificăm dacă există erori de validare (Regex-ul telefonului, etc.)
        if (result.hasErrors()) {
            // Trebuie să reîncărcăm utilizatorul pentru a avea acces la el în formular
            Utilizator utilizator = utilizatorRepository.findById(utilizatorId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilizator negăsit"));

            model.addAttribute("utilizator", utilizator);
            // Returnăm pagina de editare. Aici Thymeleaf va afișa erorile prin th:errors
            return "profil-utilizator";
        }

        // 2. Logica de salvare (doar dacă datele sunt valide)
        Utilizator utilizator = utilizatorRepository.findById(utilizatorId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator negăsit"));

        if (utilizator.getProfil() != null) {
            profil.setId(utilizator.getProfil().getId());
        }

        profil.setUtilizator(utilizator);
        utilizator.setProfil(profil);

        utilizatorRepository.save(utilizator);

        redirectAttributes.addFlashAttribute("success", "Profil actualizat cu succes!");
        return "redirect:/utilizatori";
    }

    @GetMapping("/editeaza-profil/{id}")
    public String editeazaProfil(@PathVariable Long id, Model model, Principal principal) {
        // 1. Căutăm utilizatorul
        Utilizator utilizator = utilizatorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator invalid"));

        // 2. Verificăm permisiunile: este admin/manager SAU este chiar el?
        boolean isAdminOrManager = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        if (!isAdminOrManager && !utilizator.getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("Nu ai voie să modifici acest profil!");
        }

        // 3. Verificăm dacă avem deja un obiect "profil" în model (adus de la o validare eșuată)
        // Dacă NU avem, atunci inițializăm un profil nou sau luăm profilul existent din DB
        if (!model.containsAttribute("profil")) {
            if (utilizator.getProfil() == null) {
                utilizator.setProfil(new com.example.cafenea.model.ProfilUtilizator());
            }
            model.addAttribute("profil", utilizator.getProfil());
        }

        // 4. Adăugăm utilizatorul în model (pentru a avea acces la datele lui în pagină)
        model.addAttribute("utilizator", utilizator);

        return "profil-utilizator";
    }
}