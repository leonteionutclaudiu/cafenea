package com.example.cafenea.controller;

import com.example.cafenea.model.ProfilUtilizator;
import com.example.cafenea.model.Utilizator;
import com.example.cafenea.service.UtilizatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/utilizatori")
public class UtilizatorController {

    private final UtilizatorService utilizatorService;

    public UtilizatorController(UtilizatorService utilizatorService) {
        this.utilizatorService = utilizatorService;
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
    public String stergeUtilizator(@PathVariable Long id,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            boolean deletedCurrentUser = utilizatorService.stergeUtilizator(id, currentUsername);
            redirectAttributes.addFlashAttribute("success", "Utilizator sters cu succes!");

            if (deletedCurrentUser) {
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                return "redirect:/login?logout";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/utilizatori";
    }

    @PostMapping("/salveaza-profil")
    public String salveazaProfil(
            @Valid @ModelAttribute("profil") ProfilUtilizator profil,
            BindingResult result,
            @RequestParam("utilizatorId") Long utilizatorId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("utilizator", utilizatorService.getUtilizatorById(utilizatorId));
            return "profil-utilizator";
        }

        utilizatorService.salveazaProfil(utilizatorId, profil);

        redirectAttributes.addFlashAttribute("success", "Profil actualizat cu succes!");
        return "redirect:/utilizatori";
    }

    @GetMapping("/editeaza-profil/{id}")
    public String editeazaProfil(@PathVariable Long id, Model model, Principal principal) {
        Utilizator utilizator = utilizatorService.getUtilizatorById(id);

        boolean isAdminOrManager = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        if (!isAdminOrManager && !utilizator.getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("Nu ai voie sa modifici acest profil!");
        }

        if (!model.containsAttribute("profil")) {
            if (utilizator.getProfil() == null) {
                utilizator.setProfil(new ProfilUtilizator());
            }
            model.addAttribute("profil", utilizator.getProfil());
        }

        model.addAttribute("utilizator", utilizator);

        return "profil-utilizator";
    }
}
