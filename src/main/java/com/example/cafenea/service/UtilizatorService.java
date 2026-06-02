package com.example.cafenea.service;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilizatorService {

    private final UtilizatorRepository utilizatorRepository;

    public UtilizatorService(UtilizatorRepository utilizatorRepository) {
        this.utilizatorRepository = utilizatorRepository;
    }

    public List<Utilizator> getAllUtilizatori() {
        return utilizatorRepository.findAll();
    }

    public Page<Utilizator> getUtilizatoriPaginati(String keyword, int page, int size, String sortField, String sortDir) {
        // Configurăm direcția de sortare (Ascendent / Descendent)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        // Pagina în Spring începe de la 0, de aceea facem page - 1
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if (keyword != null && !keyword.isEmpty()) {
            return utilizatorRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
        }

        return utilizatorRepository.findAll(pageable);
    }
}