package com.example.cafenea.repository;

import com.example.cafenea.model.Produs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdusRepository extends JpaRepository<Produs, Long> {
    Page<Produs> findByNumeContainingIgnoreCase(String nume, Pageable pageable);
    Page<Produs> findByCategorieId(Long categorieId, Pageable pageable);
    Page<Produs> findByNumeContainingIgnoreCaseAndCategorieId(String nume, Long categorieId, Pageable pageable);
}