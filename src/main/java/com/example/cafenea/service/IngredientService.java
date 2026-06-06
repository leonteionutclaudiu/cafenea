// IngredientService.java
package com.example.cafenea.service;

import com.example.cafenea.model.Ingredient;
import com.example.cafenea.repository.IngredientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class IngredientService {
    private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    // Metodă nouă pentru Paginare, Sortare și Căutare
    public Page<Ingredient> getIngredientePaginate(String keyword, int page, int size, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if (keyword != null && !keyword.isEmpty()) {
            return ingredientRepository.findByNumeIngredientContainingIgnoreCase(keyword, pageable);
        }

        return ingredientRepository.findAll(pageable);
    }
    public List<Ingredient> getAllIngredients() {
        logger.info("Se solicita lista completa de ingrediente.");
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Long id) {
        logger.debug("Se cauta ingredientul cu ID-ul {}.", id);
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredientul cu ID-ul " + id + " nu exista."));
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        logger.info("Se salveaza ingredientul {} cu stoc {}.", ingredient.getNumeIngredient(), ingredient.getCantitateStoc());
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            logger.error("S-a incercat stergerea unui ingredient inexistent. ID: {}", id);
            throw new IllegalArgumentException("Nu se poate sterge. Ingredientul nu exista.");
        }
        logger.warn("Se sterge ingredientul cu ID-ul {}.", id);
        ingredientRepository.deleteById(id);
    }
}
