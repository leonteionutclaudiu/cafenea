package com.example.cafenea;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.model.ProfilUtilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.service.UtilizatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilizatorServiceTest {

    @Mock
    private UtilizatorRepository utilizatorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void inregistreazaUtilizatorCripteazaParola() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("ospatar");
        utilizator.setPassword("parola123");
        utilizator.setRol("USER");

        when(utilizatorRepository.findByUsername("ospatar")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("parola123")).thenReturn("hash");

        service.inregistreazaUtilizator(utilizator);

        verify(utilizatorRepository).save(utilizator);
    }

    @Test
    void inregistreazaUtilizatorDuplicatAruncaExceptie() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("manager");
        utilizator.setPassword("manager123");
        utilizator.setRol("ADMIN");

        when(utilizatorRepository.findByUsername("manager")).thenReturn(Optional.of(utilizator));

        assertThrows(IllegalArgumentException.class, () -> service.inregistreazaUtilizator(utilizator));
        verify(utilizatorRepository, never()).save(utilizator);
    }

    @Test
    void schimbaParolaRespingeConfirmareDiferita() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);

        assertThrows(IllegalArgumentException.class,
                () -> service.schimbaParola("manager", "oldpass", "parola123", "alta123"));
    }

    @Test
    void getAllUtilizatoriReturneazaLista() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        when(utilizatorRepository.findAll()).thenReturn(List.of(new Utilizator()));

        assertEquals(1, service.getAllUtilizatori().size());
    }

    @Test
    void getUtilizatoriPaginatiCuKeywordFolosesteCautare() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        when(utilizatorRepository.findByUsernameContainingIgnoreCase(eq("man"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Utilizator())));

        assertEquals(1, service.getUtilizatoriPaginati("man", 1, 5, "username", "asc").getTotalElements());
    }

    @Test
    void getUtilizatoriPaginatiFaraKeywordReturneazaToti() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        when(utilizatorRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new Utilizator())));

        assertEquals(1, service.getUtilizatoriPaginati("", 1, 5, "username", "desc").getTotalElements());
    }

    @Test
    void getUtilizatorByIdReturneazaUtilizatorul() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        when(utilizatorRepository.findById(1L)).thenReturn(Optional.of(utilizator));

        assertEquals(utilizator, service.getUtilizatorById(1L));
    }

    @Test
    void getUtilizatorByUsernameAruncaExceptieCandNuExista() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        when(utilizatorRepository.findByUsername("nimeni")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getUtilizatorByUsername("nimeni"));
    }

    @Test
    void schimbaParolaCuDateCorecteSalveazaUtilizatorul() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("manager");
        utilizator.setPassword("hash-vechi");

        when(utilizatorRepository.findByUsername("manager")).thenReturn(Optional.of(utilizator));
        when(passwordEncoder.matches("veche123", "hash-vechi")).thenReturn(true);
        when(passwordEncoder.encode("noua123")).thenReturn("hash-nou");

        service.schimbaParola("manager", "veche123", "noua123", "noua123");

        verify(utilizatorRepository).save(utilizator);
    }

    @Test
    void schimbaParolaRespingeParolaCurentaGresita() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("manager");
        utilizator.setPassword("hash-vechi");

        when(utilizatorRepository.findByUsername("manager")).thenReturn(Optional.of(utilizator));
        when(passwordEncoder.matches("gresita", "hash-vechi")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.schimbaParola("manager", "gresita", "noua123", "noua123"));
    }

    @Test
    void stergeUtilizatorReturneazaTrueCandSeStergeUserulCurent() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("manager");
        when(utilizatorRepository.findById(1L)).thenReturn(Optional.of(utilizator));

        assertTrue(service.stergeUtilizator(1L, "manager"));
        verify(utilizatorRepository).deleteById(1L);
    }

    @Test
    void stergeUtilizatorReturneazaFalseCandSeStergeAltUser() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("ospatar");
        when(utilizatorRepository.findById(2L)).thenReturn(Optional.of(utilizator));

        assertFalse(service.stergeUtilizator(2L, "manager"));
    }

    @Test
    void salveazaProfilPastreazaIdulProfiluluiExistent() {
        UtilizatorService service = new UtilizatorService(utilizatorRepository, passwordEncoder);
        Utilizator utilizator = new Utilizator();
        ProfilUtilizator profilExistent = new ProfilUtilizator();
        profilExistent.setId(10L);
        utilizator.setProfil(profilExistent);

        ProfilUtilizator profilNou = new ProfilUtilizator();
        profilNou.setNumeComplet("Manager Cafenea");

        when(utilizatorRepository.findById(1L)).thenReturn(Optional.of(utilizator));

        service.salveazaProfil(1L, profilNou);

        assertEquals(10L, profilNou.getId());
        verify(utilizatorRepository).save(utilizator);
    }
}
