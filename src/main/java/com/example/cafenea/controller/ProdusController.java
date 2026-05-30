package com.example.cafenea.controller;

import com.example.cafenea.model.Produs;
import com.example.cafenea.service.ProdusService;
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

    // Ruta pentru pagina personalizată de Login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Ruta principală: Afișează tabelul cu produse, având Paginare și Sortare (Cerința 7)
    @GetMapping("/produse")
    public String listeazaProduse(Model model,
                                  @RequestParam(defaultValue = "0") int pagina,
                                  @RequestParam(defaultValue = "nume") String sortare,
                                  @RequestParam(required = false) String cautare) { // Parametru nou pentru căutare

        Page<Produs> paginaProduse = produsService.getProdusePaginate(pagina, 3, sortare, cautare);

        model.addAttribute("listaProduse", paginaProduse.getContent());
        model.addAttribute("paginaCurenta", pagina);
        model.addAttribute("totalPagini", paginaProduse.getTotalPages());
        model.addAttribute("sortare", sortare);
        model.addAttribute("cautare", cautare); // Îl trimitem înapoi în HTML ca să rămână scris în căsuță
        return "produse";
    }

    // Ruta care deschide formularul de adăugare produs nou
    @GetMapping("/produse/nou")
    public String formularProdusNou(Model model) {
        model.addAttribute("produs", new Produs());
        return "formular-produs"; // Va căuta fișierul formular-produs.html
    }

    // Ruta care salvează produsul și verifică validările server-side (Cerința 5 - Views & Validation)
    @PostMapping("/produse/salveaza")
    public String salveazaProdus(@Valid @ModelAttribute("produs") Produs produs, BindingResult result) {
        if (result.hasErrors()) {
            return "formular-produs"; // Dacă prețul e < 1 sau numele e gol, reîncărcăm formularul cu erori
        }
        produsService.salveazaProdus(produs);
        return "redirect:/produse"; // După salvare, ne întoarcem la tabelul cu produse
    }

    // Ruta pentru ștergerea unui produs (Securizată în SecurityConfig doar pentru ADMIN)
    @GetMapping("/produse/sterge/{id}")
    public String stergeProdus(@PathVariable Long id) {
        produsService.stergeProdus(id);
        return "redirect:/produse";
    }
}