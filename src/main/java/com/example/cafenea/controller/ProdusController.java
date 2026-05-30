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

    // Ruta principală: Afișează tabelul cu produse, având Paginare și Sortare
    @GetMapping("/produse")
    public String listeazaProduse(Model model,
                                  @RequestParam(defaultValue = "0") int pagina,
                                  @RequestParam(defaultValue = "nume") String sortare,
                                  @RequestParam(required = false) String cautare,
                                  @RequestParam(required = false) Long categorieId) { // Parametru nou pentru filtru

        // Trimitem parametrul în service (trebuie să modificăm și acolo metoda)
        Page<Produs> paginaProduse = produsService.getProdusePaginate(pagina, 3, sortare, cautare, categorieId);

        model.addAttribute("listaProduse", paginaProduse.getContent());
        model.addAttribute("paginaCurenta", pagina);
        model.addAttribute("totalPagini", paginaProduse.getTotalPages());
        model.addAttribute("sortare", sortare);
        model.addAttribute("cautare", cautare);
        model.addAttribute("categorieSelectata", categorieId); // Îl păstrăm ca să rămână selectat în dropdown după submit
        model.addAttribute("categorii", categorieProdusRepository.findAll()); // Trimitem toate categoriile pentru filtru

        return "produse";
    }

    // Ruta care deschide formularul de adăugare produs nou
    @GetMapping("/produse/nou")
    public String formularProdusNou(Model model) {
        model.addAttribute("produs", new Produs());
        model.addAttribute("categorii", categorieProdusRepository.findAll());
        return "formular-produs";
    }

    // REPARAT: Am înlocuit „...” cu obiectul real produsExistent
    @GetMapping("/produse/editeaza/{id}")
    public String formularEditareProdus(@PathVariable Long id, Model model) {
        Produs produsExistent = produsService.getProdusDupaId(id);

        model.addAttribute("produs", produsExistent); // Trimitem produsul precompletat cu tot cu ID
        model.addAttribute("categorii", categorieProdusRepository.findAll()); // CRUCIAL: Trimitem categoriile pentru dropdown!
        return "formular-produs"; // Numele exact al fișierului tău HTML pentru formular
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