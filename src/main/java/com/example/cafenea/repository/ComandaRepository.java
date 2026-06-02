package com.example.cafenea.repository;

import com.example.cafenea.model.Comanda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    Page<String> findByStatusContainingIgnoreCase(String status, Pageable pageable);
}