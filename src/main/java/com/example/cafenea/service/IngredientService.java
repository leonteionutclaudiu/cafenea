// IngredientService.java
package com.example.cafenea.service;

import com.example.cafenea.model.Ingredient;
import com.example.cafenea.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class IngredientService {
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
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredientul cu ID-ul " + id + " nu exista."));
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new IllegalArgumentException("Nu se poate sterge. Ingredientul nu exista.");
        }
        ingredientRepository.deleteById(id);
    }
}