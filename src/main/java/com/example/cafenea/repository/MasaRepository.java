package com.example.cafenea.repository;

import com.example.cafenea.model.Masa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasaRepository extends JpaRepository<Masa, Long> {
    Optional<Masa> findByNumarMasa(Integer numarMasa);
}
