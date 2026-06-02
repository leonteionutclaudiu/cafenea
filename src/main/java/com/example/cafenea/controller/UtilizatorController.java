package com.example.cafenea.controller;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.service.UtilizatorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}