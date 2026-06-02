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
    // 2. READ cu PAGINARE, SORTARE și FILTRARE (Cerința 7 din proiect) - REPARATĂ ALINIEREA CU CONTROLLER-UL
    public Page<Produs> getProdusePaginate(String cautare, Long categorieId, int pagina, int dimensiune, String sortCamp, String sortDirectie) {

        // Generăm sortarea dinamică în funcție de direcția cerută (asc / desc)
        Sort sort = sortDirectie.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortCamp).ascending() : Sort.by(sortCamp).descending();

        // Spring Data JPA începe paginarea de la 0. De aceea scădem 1 din pagina primită de la Controller (care vine ca 1, 2, 3...)
        Pageable pageable = PageRequest.of(pagina - 1, dimensiune, sort);

        boolean areCautare = (cautare != null && !cautare.trim().isEmpty());
        boolean areCategorie = (categorieId != null);

        // 1. Filtrare completă: și după text și după categorie
        if (areCautare && areCategorie) {
            return produsRepository.findByNumeContainingIgnoreCaseAndCategorieId(cautare, categorieId, pageable);
        }
        // 2. Filtrare doar după categorie
        else if (areCategorie) {
            return produsRepository.findByCategorieId(categorieId, pageable);
        }
        // 3. Filtrare doar după text
        else if (areCautare) {
            return produsRepository.findByNumeContainingIgnoreCase(cautare, pageable);
        }
        // 4. Niciun filtru aplicat -> aduce toate produsele sortate și paginate
        else {
            return produsRepository.findAll(pageable);
        }
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