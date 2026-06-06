package com.example.cafenea.service;

import com.example.cafenea.model.Masa;
import com.example.cafenea.repository.MasaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasaService {
    private static final Logger logger = LoggerFactory.getLogger(MasaService.class);

    private final MasaRepository masaRepository;

    public MasaService(MasaRepository masaRepository) {
        this.masaRepository = masaRepository;
    }

    public List<Masa> getAllMese() {
        logger.info("Se solicita lista completa de mese.");
        return masaRepository.findAll();
    }

    public Masa getMasaById(Long id) {
        logger.debug("Se cauta masa cu ID-ul {}.", id);
        return masaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Masa cu ID-ul " + id + " nu exista."));
    }

    public void salveazaMasa(Masa masa) {
        masaRepository.findByNumarMasa(masa.getNumarMasa())
                .filter(m -> !m.getId().equals(masa.getId()))
                .ifPresent(m -> {
                    logger.error("Numar de masa duplicat: {}.", masa.getNumarMasa());
                    throw new IllegalArgumentException("Exista deja o masa cu acest numar.");
                });

        logger.info("Se salveaza masa {} cu status {}.", masa.getNumarMasa(), masa.getStatus());
        masaRepository.save(masa);
    }

    public void stergeMasa(Long id) {
        Masa masa = getMasaById(id);
        if ("OCUPATA".equals(masa.getStatus())) {
            logger.error("S-a incercat stergerea mesei ocupate cu ID-ul {}.", id);
            throw new IllegalArgumentException("Masa ocupata nu poate fi stearsa.");
        }
        logger.warn("Se sterge masa cu ID-ul {}.", id);
        masaRepository.deleteById(id);
    }
}
