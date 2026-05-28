package com.example.cafenea.repository;

import com.example.cafenea.model.Produs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdusRepository extends JpaRepository<Produs, Long> {
    // Pageable preia automat parametrii de paginare și sortare din interfață (Cerința 7)
    Page<Produs> findAll(Pageable pageable);
}