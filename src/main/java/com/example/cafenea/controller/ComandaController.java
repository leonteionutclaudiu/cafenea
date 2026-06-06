package com.example.cafenea.controller;

import com.example.cafenea.model.Comanda;
import com.example.cafenea.model.Masa;
import com.example.cafenea.service.ComandaService;
import com.example.cafenea.service.MasaService;
import com.example.cafenea.service.ProdusService;
import com.example.cafenea.service.UtilizatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/comenzi")
public class ComandaController {

    private final ComandaService comandaService;
    private final ProdusService produsService;
    private final UtilizatorService utilizatorService;
    private final MasaService masaService;

    public ComandaController(ComandaService comandaService,
                             ProdusService produsService,
                             UtilizatorService utilizatorService,
                             MasaService masaService) {
        this.comandaService = comandaService;
        this.produsService = produsService;
        this.utilizatorService = utilizatorService;
        this.masaService = masaService;
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
        addFormData(model);
        return "formular-comanda";
    }

    @PostMapping("/salveaza")
    public String salveazaComanda(@Valid @ModelAttribute("comanda") Comanda comanda,
                                  BindingResult result,
                                  @RequestParam(value = "produseIds", required = false) List<Long> produseIds,
                                  HttpServletRequest request,
                                  Model model) {

        if (produseIds == null || produseIds.isEmpty()) {
            result.rejectValue("produse", "produse.empty", "Selectati cel putin un produs pentru comanda.");
        }

        if (result.hasErrors()) {
            addFormData(model);
            return "formular-comanda";
        }

        if (comanda.getId() != null) {
            Comanda comandaVeche = comandaService.getComandaById(comanda.getId());
            Masa masaVeche = comandaVeche.getMasa();

            if (masaVeche != null && (comanda.getMasa() == null || !masaVeche.getId().equals(comanda.getMasa().getId()))) {
                masaVeche.setStatus("LIBERA");
                masaService.salveazaMasa(masaVeche);
            }
        }

        if (comanda.getMasa() != null && comanda.getMasa().getId() != null) {
            Masa masaNoua = masaService.getMasaById(comanda.getMasa().getId());
            comanda.setMasa(masaNoua);
            masaNoua.setStatus("OCUPATA");
            masaService.salveazaMasa(masaNoua);
        } else {
            comanda.setMasa(null);
        }

        List<Long> listaIduriCuDuplicate = new ArrayList<>();
        for (Long pId : produseIds) {
            String cantitateStr = request.getParameter("cantitate_" + pId);
            int cantitate = (cantitateStr != null && !cantitateStr.isEmpty()) ? Integer.parseInt(cantitateStr) : 1;
            for (int i = 0; i < cantitate; i++) {
                listaIduriCuDuplicate.add(pId);
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
                masaService.salveazaMasa(masa);
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
            masaService.salveazaMasa(masa);
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
        addFormData(model);
        return "formular-comanda";
    }

    private void addFormData(Model model) {
        model.addAttribute("utilizatori", utilizatorService.getAllUtilizatori());
        model.addAttribute("toateProdusele", produsService.getAllProduse());
        model.addAttribute("toateMesele", masaService.getAllMese());
    }
}
