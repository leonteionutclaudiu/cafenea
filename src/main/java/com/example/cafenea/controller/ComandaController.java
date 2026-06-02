package com.example.cafenea.controller;

import com.example.cafenea.model.Comanda;
import com.example.cafenea.model.Masa;
import com.example.cafenea.service.ComandaService;
import com.example.cafenea.service.ProdusService;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.repository.MasaRepository;
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

    private final MasaRepository masaRepository;
    private final ComandaService comandaService;
    private final ProdusService produsService;
    private final UtilizatorRepository utilizatorRepository;

    public ComandaController(ComandaService comandaService,
                             ProdusService produsService,
                             UtilizatorRepository utilizatorRepository,
                             MasaRepository masaRepository) {
        this.comandaService = comandaService;
        this.produsService = produsService;
        this.utilizatorRepository = utilizatorRepository;
        this.masaRepository = masaRepository;
    }

    @GetMapping
    public String listeazaComenzi(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dataComanda") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        int currentPage = (page == null || page < 1) ? 1 : page;

        Page<Comanda> pageComenzi = comandaService.getComenziPaginate(currentPage, size, sortField, sortDir);
        model.addAttribute("listaComenzi", pageComenzi.getContent());
        model.addAttribute("currentPage", currentPage);
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
        model.addAttribute("toateMesele", masaRepository.findAll());
        return "formular-comanda";
    }

    @PostMapping("/salveaza")
    public String salveazaComanda(@ModelAttribute("comanda") Comanda comanda,
                                  @RequestParam(value = "produseIds", required = false) List<Long> produseIds,
                                  HttpServletRequest request) {

        // Logica pentru schimbarea mesei la editare
        if (comanda.getId() != null) {
            Comanda comandaVeche = comandaService.getComandaById(comanda.getId());
            Masa masaVeche = comandaVeche.getMasa();

            // Dacă masa s-a schimbat, eliberăm masa veche
            if (masaVeche != null && comanda.getMasa() != null && !masaVeche.getId().equals(comanda.getMasa().getId())) {
                masaVeche.setStatus("LIBERA");
                masaRepository.save(masaVeche);
            }
        }

        // Setăm masa nouă ca ocupată
        if (comanda.getMasa() != null && comanda.getMasa().getId() != null) {
            Masa masaNoua = masaRepository.findById(comanda.getMasa().getId()).orElse(null);
            if (masaNoua != null) {
                comanda.setMasa(masaNoua);
                masaNoua.setStatus("OCUPATA");
                masaRepository.save(masaNoua);
            }
        }

        // Logica de prelucrare a produselor
        List<Long> listaIduriCuDuplicate = new java.util.ArrayList<>();
        if (produseIds != null) {
            for (Long pId : produseIds) {
                String cantitateStr = request.getParameter("cantitate_" + pId);
                int cantitate = (cantitateStr != null && !cantitateStr.isEmpty()) ? Integer.parseInt(cantitateStr) : 1;
                for (int i = 0; i < cantitate; i++) listaIduriCuDuplicate.add(pId);
            }
        }

        comandaService.salveazaComanda(comanda, listaIduriCuDuplicate);
        return "redirect:/comenzi";
    }

    @GetMapping("/status/{id}")
    public String schimbaStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        Comanda comanda = comandaService.getComandaById(id);

        if ("FINALIZATA".equals(status) || "ANULATA".equals(status)) {
            Masa masa = comanda.getMasa();
            if (masa != null) {
                masa.setStatus("LIBERA");
                masaRepository.save(masa);
            }
        }

        comandaService.schimbaStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Status actualizat!");
        return "redirect:/comenzi";
    }

    @GetMapping("/sterge/{id}")
    public String sterge(@PathVariable Long id) {
        Comanda comanda = comandaService.getComandaById(id);
        if (comanda.getMasa() != null) {
            Masa masa = comanda.getMasa();
            masa.setStatus("LIBERA");
            masaRepository.save(masa);
        }
        comandaService.stergeComanda(id);
        return "redirect:/comenzi";
    }

    @GetMapping("/editeaza/{id}")
    public String formularEditareComanda(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Comanda comandaExistenta = comandaService.getComandaById(id);
        if ("FINALIZATA".equals(comandaExistenta.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Comenzile finalizate nu pot fi modificate!");
            return "redirect:/comenzi";
        }
        model.addAttribute("comanda", comandaExistenta);
        model.addAttribute("utilizatori", utilizatorRepository.findAll());
        model.addAttribute("toateProdusele", produsService.getAllProduse());
        model.addAttribute("toateMesele", masaRepository.findAll());
        return "formular-comanda";
    }
}