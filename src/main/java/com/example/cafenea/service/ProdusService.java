package com.example.cafenea.service;

import com.example.cafenea.model.Produs;
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

    // Inițializăm logger-ul SLF4J conform Cerinței 6 din proiect
    private static final Logger logger = LoggerFactory.getLogger(ProdusService.class);

    @Autowired
    private ProdusRepository produsRepository;

    // 1. READ: Toate produsele (fără paginare, util pentru liste simple)
    public List<Produs> getAllProduse() {
        logger.info("LOG INFO: S-a solicitat lista completa de produse din cafenea.");
        return produsRepository.findAll();
    }

    // 2. READ cu PAGINARE și SORTARE (Cerința 7 din proiect)
    public Page<Produs> getProdusePaginate(int pagina, int dimensiune, String sortare, String cautare) {
        Pageable pageable = PageRequest.of(pagina, dimensiune, Sort.by(sortare));

        // Dacă există text în căsuța de căutare, filtrăm. Dacă nu, aducem toate produsele.
        if (cautare != null && !cautare.trim().isEmpty()) {
            return produsRepository.findByNumeContainingIgnoreCase(cautare, pageable);
        }
        return produsRepository.findAll(pageable);
    }

    // 3. READ după ID (pentru editare sau detalii)
    public Optional<Produs> getProdusById(Long id) {
        logger.debug("LOG DEBUG: Se cauta produsul cu ID-ul: {}", id);
        return produsRepository.findById(id);
    }

    // 4. CREATE & UPDATE: Salvare/Modificare produs (Cerința 2)
    public void salveazaProdus(Produs produs) {
        logger.info("LOG INFO: S-a adaugat/actualizat produsul în meniu: {} cu pretul de {} RON", produs.getNume(), produs.getPret());
        produsRepository.save(produs);
    }

    // 5. DELETE: Ștergere produs din meniu cu tratarea excepțiilor (Cerința 2)
    public void stergeProdus(Long id) {
        if (produsRepository.existsById(id)) {
            logger.warn("LOG WARN: Se sterge definitiv produsul cu ID-ul: {}", id);
            produsRepository.deleteById(id);
        } else {
            // Dacă produsul nu există, aruncăm o excepție specifică și o logăm ca ERROR
            logger.error("LOG ERROR: S-a incercat stergerea unui produs care nu exista! ID: {}", id);
            throw new IllegalArgumentException("Produsul cu ID-ul " + id + " nu a fost gasit in baza de date.");
        }
    }

    public Produs getProdusDupaId(Long id) {
        return produsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produsul cu ID-ul " + id + " nu există!"));
    }
}