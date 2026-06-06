package com.example.cafenea.service;

import com.example.cafenea.model.Masa;
import com.example.cafenea.repository.MasaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasaService {

    private final MasaRepository masaRepository;

    public MasaService(MasaRepository masaRepository) {
        this.masaRepository = masaRepository;
    }

    public List<Masa> getAllMese() {
        return masaRepository.findAll();
    }

    public Masa getMasaById(Long id) {
        return masaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Masa cu ID-ul " + id + " nu exista."));
    }

    public void salveazaMasa(Masa masa) {
        masaRepository.findByNumarMasa(masa.getNumarMasa())
                .filter(m -> !m.getId().equals(masa.getId()))
                .ifPresent(m -> {
                    throw new IllegalArgumentException("Exista deja o masa cu acest numar.");
                });

        masaRepository.save(masa);
    }

    public void stergeMasa(Long id) {
        Masa masa = getMasaById(id);
        if ("OCUPATA".equals(masa.getStatus())) {
            throw new IllegalArgumentException("Masa ocupata nu poate fi stearsa.");
        }
        masaRepository.deleteById(id);
    }
}
