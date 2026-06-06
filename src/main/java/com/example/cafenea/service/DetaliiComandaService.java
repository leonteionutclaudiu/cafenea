package com.example.cafenea.service;

import com.example.cafenea.model.DetaliiComanda;
import com.example.cafenea.repository.DetaliiComandaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetaliiComandaService {

    private static final Logger logger = LoggerFactory.getLogger(DetaliiComandaService.class);

    private final DetaliiComandaRepository detaliiComandaRepository;

    public DetaliiComandaService(DetaliiComandaRepository detaliiComandaRepository) {
        this.detaliiComandaRepository = detaliiComandaRepository;
    }

    public List<DetaliiComanda> getAllDetalii() {
        logger.info("Se solicita lista completa de detalii comanda.");
        return detaliiComandaRepository.findAll();
    }

    public DetaliiComanda getDetaliuById(Long id) {
        logger.debug("Se cauta detaliul de comanda cu ID-ul {}.", id);
        return detaliiComandaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Detaliul de comanda cu ID-ul " + id + " nu exista."));
    }

    @Transactional
    public void salveazaDetaliu(DetaliiComanda detaliu) {
        if (detaliu.getProdus() == null) {
            throw new IllegalArgumentException("Produsul este obligatoriu.");
        }

        if (detaliu.getPretSalvat() == null || detaliu.getPretSalvat() <= 0) {
            detaliu.setPretSalvat(detaliu.getProdus().getPret());
        }

        logger.info("Se salveaza detaliul de comanda pentru produsul {}.", detaliu.getProdus().getNume());
        detaliiComandaRepository.save(detaliu);
    }

    @Transactional
    public void stergeDetaliu(Long id) {
        if (!detaliiComandaRepository.existsById(id)) {
            logger.error("S-a incercat stergerea unui detaliu de comanda inexistent. ID: {}", id);
            throw new IllegalArgumentException("Detaliul de comanda cu ID-ul " + id + " nu exista.");
        }

        logger.warn("Se sterge detaliul de comanda cu ID-ul {}.", id);
        detaliiComandaRepository.deleteById(id);
    }
}
