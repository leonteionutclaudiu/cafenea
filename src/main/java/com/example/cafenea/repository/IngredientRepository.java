package com.example.cafenea.repository;

import com.example.cafenea.model.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Page<Ingredient> findByNumeIngredientContainingIgnoreCase(String numeIngredient, Pageable pageable);
}