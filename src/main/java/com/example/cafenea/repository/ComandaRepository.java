package com.example.cafenea.repository;

import com.example.cafenea.model.Comanda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    @Override
    @EntityGraph(attributePaths = {"produse", "masa", "utilizator"})
    Page<Comanda> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"produse", "masa", "utilizator"})
    Optional<Comanda> findById(Long id);

    Page<String> findByStatusContainingIgnoreCase(String status, Pageable pageable);
    boolean existsByProduse_Id(Long produsId);
    boolean existsByMasa_Id(Long masaId);
}
