package com.example.cafenea.controller;

import com.example.cafenea.model.DetaliiComanda;
import com.example.cafenea.service.ComandaService;
import com.example.cafenea.service.DetaliiComandaService;
import com.example.cafenea.service.ProdusService;
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
@RequestMapping("/detalii-comenzi")
@PreAuthorize("hasRole('ADMIN')")
public class DetaliiComandaController {

    private final DetaliiComandaService detaliiComandaService;
    private final ComandaService comandaService;
    private final ProdusService produsService;

    public DetaliiComandaController(DetaliiComandaService detaliiComandaService,
                                    ComandaService comandaService,
                                    ProdusService produsService) {
        this.detaliiComandaService = detaliiComandaService;
        this.comandaService = comandaService;
        this.produsService = produsService;
    }

    @GetMapping
    public String listeazaDetalii(Model model) {
        model.addAttribute("detalii", detaliiComandaService.getAllDetalii());
        return "detalii-comenzi";
    }

    @GetMapping("/nou")
    public String formularDetaliuNou(Model model) {
        model.addAttribute("detaliu", new DetaliiComanda());
        adaugaListeSelect(model);
        return "formular-detaliu-comanda";
    }

    @GetMapping("/editeaza/{id}")
    public String formularEditare(@PathVariable Long id, Model model) {
        model.addAttribute("detaliu", detaliiComandaService.getDetaliuById(id));
        adaugaListeSelect(model);
        return "formular-detaliu-comanda";
    }

    @PostMapping("/salveaza")
    public String salveazaDetaliu(@Valid @ModelAttribute("detaliu") DetaliiComanda detaliu,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            adaugaListeSelect(model);
            return "formular-detaliu-comanda";
        }

        try {
            detaliiComandaService.salveazaDetaliu(detaliu);
        } catch (IllegalArgumentException e) {
            model.addAttribute("eroare", e.getMessage());
            adaugaListeSelect(model);
            return "formular-detaliu-comanda";
        }

        return "redirect:/detalii-comenzi";
    }

    @GetMapping("/sterge/{id}")
    public String stergeDetaliu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            detaliiComandaService.stergeDetaliu(id);
            redirectAttributes.addFlashAttribute("success", "Detaliul comenzii a fost sters cu succes.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/detalii-comenzi";
    }

    private void adaugaListeSelect(Model model) {
        model.addAttribute("comenzi", comandaService.getAllComenzi());
        model.addAttribute("produse", produsService.getAllProduse());
    }
}
