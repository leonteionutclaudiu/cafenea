package com.example.cafenea.repository;

import com.example.cafenea.model.CategorieProdus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieProdusRepository extends JpaRepository<CategorieProdus, Long> {
}
