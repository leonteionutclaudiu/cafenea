package com.example.cafenea.service;

import com.example.cafenea.model.ProfilUtilizator;
import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilizatorService {
    private static final Logger logger = LoggerFactory.getLogger(UtilizatorService.class);

    private final UtilizatorRepository utilizatorRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilizatorService(UtilizatorRepository utilizatorRepository, PasswordEncoder passwordEncoder) {
        this.utilizatorRepository = utilizatorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Utilizator> getAllUtilizatori() {
        logger.info("Se solicita lista completa de utilizatori.");
        return utilizatorRepository.findAll();
    }

    public Page<Utilizator> getUtilizatoriPaginati(String keyword, int page, int size, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if (keyword != null && !keyword.isEmpty()) {
            logger.debug("Se cauta utilizatori dupa keyword '{}'.", keyword);
            return utilizatorRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
        }

        return utilizatorRepository.findAll(pageable);
    }

    public Utilizator getUtilizatorById(Long id) {
        logger.debug("Se cauta utilizatorul cu ID-ul {}.", id);
        return utilizatorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul cu ID-ul " + id + " nu exista."));
    }

    public Utilizator getUtilizatorByUsername(String username) {
        logger.debug("Se cauta utilizatorul cu username '{}'.", username);
        return utilizatorRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul " + username + " nu exista."));
    }

    public void inregistreazaUtilizator(Utilizator utilizator) {
        if (utilizatorRepository.findByUsername(utilizator.getUsername()).isPresent()) {
            logger.error("S-a incercat crearea unui username duplicat: {}.", utilizator.getUsername());
            throw new IllegalArgumentException("Acest nume de utilizator este deja utilizat!");
        }

        utilizator.setPassword(passwordEncoder.encode(utilizator.getPassword()));
        logger.info("Se creeaza utilizatorul {} cu rol {}.", utilizator.getUsername(), utilizator.getRol());
        utilizatorRepository.save(utilizator);
    }

    public void schimbaParola(String username, String parolaActuala, String parolaNoua, String confirmaParolaNoua) {
        if (parolaNoua == null || parolaNoua.length() < 6) {
            throw new IllegalArgumentException("Noua parola trebuie sa aiba minimum 6 caractere!");
        }

        if (!parolaNoua.equals(confirmaParolaNoua)) {
            throw new IllegalArgumentException("Noua parola si confirmarea ei nu coincid!");
        }

        Utilizator utilizator = getUtilizatorByUsername(username);
        if (!passwordEncoder.matches(parolaActuala, utilizator.getPassword())) {
            logger.error("Schimbare parola esuata pentru utilizatorul {}: parola curenta incorecta.", username);
            throw new IllegalArgumentException("Parola actuala introdusa este incorecta!");
        }

        utilizator.setPassword(passwordEncoder.encode(parolaNoua));
        utilizatorRepository.save(utilizator);
        logger.info("Parola a fost schimbata pentru utilizatorul {}.", username);
    }

    public boolean stergeUtilizator(Long id, String currentUsername) {
        Utilizator userToDelete = getUtilizatorById(id);
        boolean isCurrentUser = userToDelete.getUsername().equals(currentUsername);

        try {
            utilizatorRepository.deleteById(id);
            logger.warn("Utilizatorul {} a fost sters.", userToDelete.getUsername());
            return isCurrentUser;
        } catch (Exception e) {
            logger.error("Utilizatorul {} nu poate fi sters deoarece are dependinte.", userToDelete.getUsername(), e);
            throw new IllegalArgumentException("Eroare: Utilizatorul are comenzi asociate!");
        }
    }

    public void salveazaProfil(Long utilizatorId, ProfilUtilizator profil) {
        Utilizator utilizator = getUtilizatorById(utilizatorId);

        if (utilizator.getProfil() != null) {
            profil.setId(utilizator.getProfil().getId());
        }

        profil.setUtilizator(utilizator);
        utilizator.setProfil(profil);

        utilizatorRepository.save(utilizator);
        logger.info("Profilul utilizatorului {} a fost actualizat.", utilizator.getUsername());
    }
}
