package com.example.cafenea.repository;

import com.example.cafenea.model.Utilizator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilizatorRepository extends JpaRepository<Utilizator, Long> {

    // Căutare exactă după username (Necesară pentru Spring Security și autentificare)
    Optional<Utilizator> findByUsername(String username);

    // Căutare parțială cu paginare (Pe care am adăugat-o anterior pentru bara de căutare)
    Page<Utilizator> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}