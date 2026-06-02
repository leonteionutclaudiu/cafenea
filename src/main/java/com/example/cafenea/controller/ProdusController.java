package com.example.cafenea.controller;

import com.example.cafenea.model.Produs;
import com.example.cafenea.service.ProdusService;
import com.example.cafenea.repository.CategorieProdusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProdusController {

    @Autowired
    private ProdusService produsService;

    @Autowired
    private CategorieProdusRepository categorieProdusRepository;

    // Ruta principală: Afișează tabelul cu produse, având Paginare și Sortare uniformizată
    @GetMapping("/produse")
    public String listeazaProduse(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categorieId, // Corectat: required=false pentru tipuri numerice/Long
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nume") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // CORECTAT: Apelăm serviciul și salvăm rezultatul în variabila pageProduse
        Page<Produs> pageProduse = produsService.getProdusePaginate(keyword, categorieId, page, size, sortField, sortDir);

        // Trimitem datele către interfața Thymeleaf (produse.html)
        model.addAttribute("listaProduse", pageProduse.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProduse.getTotalPages());
        model.addAttribute("totalItems", pageProduse.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("categorieSelectata", categorieId);

        // CRUCIAL: Trimitem și lista completă de categorii pentru a popula dropdown-ul de filtrare din pagină
        model.addAttribute("categorii", categorieProdusRepository.findAll());

        return "produse";
    }

    // Ruta care deschide formularul de adăugare produs nou
    @GetMapping("/produse/nou")
    public String formularProdusNou(Model model) {
        model.addAttribute("produs", new Produs());
        model.addAttribute("categorii", categorieProdusRepository.findAll());
        return "formular-produs";
    }

    // Ruta care deschide formularul de editare al unui produs existent
    @GetMapping("/produse/editeaza/{id}")
    public String formularEditareProdus(@PathVariable Long id, Model model) {
        Produs produsExistent = produsService.getProdusDupaId(id);

        model.addAttribute("produs", produsExistent); // Trimitem produsul precompletat cu tot cu ID
        model.addAttribute("categorii", categorieProdusRepository.findAll()); // Trimitem categoriile pentru dropdown
        return "formular-produs";
    }

    // Ruta POST care salvează modificările sau adaugă produsul nou
    @PostMapping("/produse/salveaza")
    public String salveazaProdus(@Valid @ModelAttribute("produs") Produs produs, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorii", categorieProdusRepository.findAll());
            return "formular-produs";
        }
        produsService.salveazaProdus(produs);
        return "redirect:/produse";
    }

    // Ruta pentru ștergerea unui produs
    @GetMapping("/produse/sterge/{id}")
    public String stergeProdus(@PathVariable Long id) {
        produsService.stergeProdus(id);
        return "redirect:/produse";
    }
}