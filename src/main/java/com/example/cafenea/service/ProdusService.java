package com.example.cafenea.service;

import com.example.cafenea.model.Produs;
import com.example.cafenea.repository.ComandaRepository;
import com.example.cafenea.repository.ProdusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProdusService {

    private static final Logger logger = LoggerFactory.getLogger(ProdusService.class);

    @Autowired
    private ProdusRepository produsRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    public List<Produs> getAllProduse() {
        logger.info("LOG INFO: S-a solicitat lista completa de produse din cafenea.");
        return produsRepository.findAll();
    }

    public Page<Produs> getProdusePaginate(String cautare, Long categorieId, int pagina, int dimensiune, String sortCamp, String sortDirectie) {

        Sort sort = sortDirectie.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortCamp).ascending() : Sort.by(sortCamp).descending();

        Pageable pageable = PageRequest.of(pagina - 1, dimensiune, sort);

        boolean areCautare = (cautare != null && !cautare.trim().isEmpty());
        boolean areCategorie = (categorieId != null);

        if (areCautare && areCategorie) {
            return produsRepository.findByNumeContainingIgnoreCaseAndCategorieId(cautare, categorieId, pageable);
        } else if (areCategorie) {
            return produsRepository.findByCategorieId(categorieId, pageable);
        } else if (areCautare) {
            return produsRepository.findByNumeContainingIgnoreCase(cautare, pageable);
        } else {
            return produsRepository.findAll(pageable);
        }
    }

    public Optional<Produs> getProdusById(Long id) {
        logger.debug("LOG DEBUG: Se cauta produsul cu ID-ul: {}", id);
        return produsRepository.findById(id);
    }

    public void salveazaProdus(Produs produs) {
        logger.info("LOG INFO: S-a adaugat/actualizat produsul în meniu: {} cu pretul de {} RON", produs.getNume(), produs.getPret());
        produsRepository.save(produs);
    }

    public void stergeProdus(Long id) {
        Produs produs = produsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("LOG ERROR: S-a incercat stergerea unui produs care nu exista! ID: {}", id);
                    return new IllegalArgumentException("Produsul cu ID-ul " + id + " nu a fost gasit in baza de date.");
                });

        if (comandaRepository.existsByProduse_Id(id)) {
            logger.error("LOG ERROR: S-a incercat stergerea unui produs folosit in comenzi. ID: {}", id);
            throw new IllegalArgumentException("Nu poti sterge produsul deoarece exista deja in una sau mai multe comenzi.");
        }

        logger.warn("LOG WARN: Se sterge definitiv produsul cu ID-ul: {}", id);
        produs.getIngrediente().clear();
        produsRepository.delete(produs);
    }

    public Produs getProdusDupaId(Long id) {
        return produsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produsul cu ID-ul " + id + " nu există!"));
    }
}
