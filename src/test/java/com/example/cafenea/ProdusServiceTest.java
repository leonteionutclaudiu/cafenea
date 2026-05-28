package com.example.cafenea;

import com.example.cafenea.model.Produs;
import com.example.cafenea.repository.ProdusRepository;
import com.example.cafenea.service.ProdusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test") // Activează profilul H2 pentru teste conform cerinței 3
public class ProdusServiceTest {

    @Mock
    private ProdusRepository produsRepository; // Simulăm repository-ul prin Mockito

    @InjectMocks
    private ProdusService produsService; // Injectăm simularea în serviciul nostru

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inițializăm simulările înainte de fiecare test
    }

    @Test
    void testSalveazaProdus() {
        // 1. Pregătim datele de test
        Produs p = new Produs();
        p.setNume("Cappuccino");
        p.setPret(14.5);

        // 2. Apelăm metoda din serviciu
        produsService.salveazaProdus(p);

        // 3. Verificăm dacă serviciul a dat mai departe comanda de salvare către Repository
        verify(produsRepository, times(1)).save(p);
    }

    @Test
    void testGetProdusById() {
        // 1. Pregătim un produs simulat în baza de date
        Produs p = new Produs();
        p.setId(1L);
        p.setNume("Espresso");
        p.setPret(10.0);

        when(produsRepository.findById(1L)).thenReturn(Optional.of(p));

        // 2. Apelăm serviciul
        Optional<Produs> rezultat = produsService.getProdusById(1L);

        // 3. Verificări de siguranță (Assertions)
        assertTrue(rezultat.isPresent());
        assertEquals("Espresso", rezultat.get().getNume());
        assertEquals(10.0, rezultat.get().getPret());
    }

    @Test
    void testStergeProdusInexistentAruncaExceptie() {
        // Simulăm că produsul cu ID-ul 999 NU există în baza de date
        when(produsRepository.existsById(999L)).thenReturn(false);

        // Verificăm dacă serviciul aruncă excepția corectă (Exception Handling cerut la punctul 2)
        assertThrows(IllegalArgumentException.class, () -> {
            produsService.stergeProdus(999L);
        });

        // Ne asigurăm că metoda de ștergere nu a fost apelată greșit
        verify(produsRepository, never()).deleteById(999L);
    }
}