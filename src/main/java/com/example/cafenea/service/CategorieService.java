package com.example.cafenea.service;

import com.example.cafenea.model.CategorieProdus;
import com.example.cafenea.repository.CategorieProdusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {

    private static final Logger logger = LoggerFactory.getLogger(CategorieService.class);

    private final CategorieProdusRepository categorieProdusRepository;

    public CategorieService(CategorieProdusRepository categorieProdusRepository) {
        this.categorieProdusRepository = categorieProdusRepository;
    }

    public List<CategorieProdus> getAllCategorii() {
        logger.info("Se solicita lista completa de categorii.");
        return categorieProdusRepository.findAll();
    }

    public CategorieProdus getCategorieById(Long id) {
        logger.debug("Se cauta categoria cu ID-ul {}.", id);
        return categorieProdusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria cu ID-ul " + id + " nu exista."));
    }

    public void salveazaCategorie(CategorieProdus categorie) {
        logger.info("Se salveaza categoria {}.", categorie.getDenumire());
        categorieProdusRepository.save(categorie);
    }

    public void stergeCategorie(Long id) {
        if (!categorieProdusRepository.existsById(id)) {
            logger.error("S-a incercat stergerea unei categorii inexistente. ID: {}", id);
            throw new IllegalArgumentException("Categoria cu ID-ul " + id + " nu exista.");
        }
        logger.warn("Se sterge categoria cu ID-ul {}.", id);
        categorieProdusRepository.deleteById(id);
    }
}
