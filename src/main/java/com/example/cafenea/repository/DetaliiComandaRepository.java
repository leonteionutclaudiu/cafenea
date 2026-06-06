package com.example.cafenea.repository;

import com.example.cafenea.model.DetaliiComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetaliiComandaRepository extends JpaRepository<DetaliiComanda, Long> {
}
