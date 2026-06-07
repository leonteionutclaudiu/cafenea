package com.example.cafenea.service;

import com.example.cafenea.model.Comanda;
import com.example.cafenea.model.Produs;
import com.example.cafenea.repository.ComandaRepository;
import com.example.cafenea.repository.ProdusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComandaService {

    private static final Logger logger = LoggerFactory.getLogger(ComandaService.class);

    private final ComandaRepository comandaRepository;
    private final ProdusRepository produsRepository;

    public ComandaService(ComandaRepository comandaRepository, ProdusRepository produsRepository) {
        this.comandaRepository = comandaRepository;
        this.produsRepository = produsRepository;
    }

    public Page<Comanda> getComenziPaginate(int page, int size, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        return comandaRepository.findAll(pageable);
    }

    public List<Comanda> getAllComenzi() {
        return comandaRepository.findAll();
    }

    @Transactional
    public void salveazaComanda(Comanda comanda, List<Long> produseIds) {
        List<Produs> listaProduseComplete = new ArrayList<>();

        if (produseIds != null) {
            for (Long pId : produseIds) {
                Produs p = produsRepository.findById(pId)
                        .orElseThrow(() -> new IllegalArgumentException("Produsul cu ID-ul " + pId + " nu există!"));
                listaProduseComplete.add(p);
            }
        }

        comanda.setProduse(listaProduseComplete);
        comanda.calculeazaTotal();

        logger.info("LOG INFO: S-a înregistrat comanda cu succes. Total de plată: {} RON", comanda.getTotalPlata());
        comandaRepository.save(comanda);
    }

    @Transactional
    public void schimbaStatus(Long id, String status) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda cu ID-ul " + id + " nu există."));

        comanda.setStatus(status);
        logger.warn("LOG WARN: Statusul comenzii {} a fost modificat în {}", id, status);
        comandaRepository.save(comanda);
    }

    @Transactional
    public void stergeComanda(Long id) {
        if (!comandaRepository.existsById(id)) {
            logger.error("LOG ERROR: S-a încercat ștergerea unei comenzi inexistente! ID: {}", id);
            throw new IllegalArgumentException("Comanda cu ID-ul " + id + " nu a fost găsită în baza de date.");
        }

        comandaRepository.deleteById(id);
        logger.info("LOG INFO: Comanda {} a fost ștearsă.", id);
    }

    public Comanda getComandaById(Long id) {
        return comandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comanda cu ID-ul " + id + " nu a fost găsită!"));
    }
}
