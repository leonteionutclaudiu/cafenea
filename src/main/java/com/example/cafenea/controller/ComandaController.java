package com.example.cafenea.controller;

import com.example.cafenea.model.Comanda;
import com.example.cafenea.service.ComandaService;
import com.example.cafenea.service.ProdusService;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comenzi")
public class ComandaController {

    private final ComandaService comandaService;
    private final ProdusService produsService;
    private final UtilizatorRepository utilizatorRepository;

    public ComandaController(ComandaService comandaService,
                             ProdusService produsService,
                             UtilizatorRepository utilizatorRepository) {
        this.comandaService = comandaService;
        this.produsService = produsService;
        this.utilizatorRepository = utilizatorRepository;
    }

    @GetMapping
    public String listeazaComenzi(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dataComanda") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Page<Comanda> pageComenzi = comandaService.getComenziPaginate(page, size, sortField, sortDir);

        model.addAttribute("listaComenzi", pageComenzi.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageComenzi.getTotalPages());
        model.addAttribute("totalItems", pageComenzi.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "comenzi";
    }

    @GetMapping("/nou")
    public String formularComanda(Model model) {
        model.addAttribute("comanda", new Comanda());
        model.addAttribute("utilizatori", utilizatorRepository.findAll());
        model.addAttribute("toateProdusele", produsService.getAllProduse());
        return "formular-comanda";
    }

    @PostMapping("/salveaza")
    public String salveazaComanda(@ModelAttribute("comanda") Comanda comanda,
                                  @RequestParam(value = "produseIds", required = false) List<Long> produseIds,
                                  HttpServletRequest request) {

        List<Long> listaIduriCuDuplicate = new java.util.ArrayList<>();

        if (produseIds != null) {
            for (Long pId : produseIds) {
                String cantitateStr = request.getParameter("cantitate_" + pId);
                int cantitate = 1;

                if (cantitateStr != null && !cantitateStr.isEmpty()) {
                    cantitate = Integer.parseInt(cantitateStr);
                }

                for (int i = 0; i < cantitate; i++) {
                    listaIduriCuDuplicate.add(pId);
                }
            }
        }

        comandaService.salveazaComanda(comanda, listaIduriCuDuplicate);
        return "redirect:/comenzi";
    }

    @GetMapping("/status/{id}")
    public String schimbaStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        // Actualizăm statusul comenzii fără logica de fidelizare
        comandaService.schimbaStatus(id, status);

        redirectAttributes.addFlashAttribute("success", "Statusul comenzii a fost actualizat cu succes!");
        return "redirect:/comenzi";
    }

    @GetMapping("/sterge/{id}")
    public String sterge(@PathVariable Long id) {
        comandaService.stergeComanda(id);
        return "redirect:/comenzi";
    }

    @GetMapping("/editeaza/{id}")
    public String formularEditareComanda(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Comanda comandaExistenta = comandaService.getComandaById(id);

        if ("FINALIZATA".equals(comandaExistenta.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Comenzile finalizate nu mai pot fi modificate!");
            return "redirect:/comenzi";
        }

        model.addAttribute("comanda", comandaExistenta);
        model.addAttribute("utilizatori", utilizatorRepository.findAll());
        model.addAttribute("toateProdusele", produsService.getAllProduse());

        return "formular-comanda";
    }
}