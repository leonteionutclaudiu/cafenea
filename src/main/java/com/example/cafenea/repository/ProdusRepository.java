package com.example.cafenea.repository;

import com.example.cafenea.model.Produs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdusRepository extends JpaRepository<Produs, Long> {
    @Override
    @EntityGraph(attributePaths = {"categorie", "ingrediente"})
    Optional<Produs> findById(Long id);

    Page<Produs> findByNumeContainingIgnoreCase(String nume, Pageable pageable);
    Page<Produs> findByCategorieId(Long categorieId, Pageable pageable);
    Page<Produs> findByNumeContainingIgnoreCaseAndCategorieId(String nume, Long categorieId, Pageable pageable);
    boolean existsByIngrediente_Id(Long ingredientId);
    boolean existsByCategorie_Id(Long categorieId);
}
