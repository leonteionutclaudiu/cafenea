package com.example.cafenea;

import com.example.cafenea.model.Produs;
import com.example.cafenea.repository.ComandaRepository;
import com.example.cafenea.repository.ProdusRepository;
import com.example.cafenea.service.ProdusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("test")
public class ProdusServiceTest {

    @Mock
    private ProdusRepository produsRepository;

    @Mock
    private ComandaRepository comandaRepository;

    @InjectMocks
    private ProdusService produsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSalveazaProdus() {
        Produs p = new Produs();
        p.setNume("Cappuccino");
        p.setPret(14.5);

        produsService.salveazaProdus(p);

        verify(produsRepository, times(1)).save(p);
    }

    @Test
    void testGetProdusById() {
        Produs p = new Produs();
        p.setId(1L);
        p.setNume("Espresso");
        p.setPret(10.0);

        when(produsRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Produs> rezultat = produsService.getProdusById(1L);

        assertTrue(rezultat.isPresent());
        assertEquals("Espresso", rezultat.get().getNume());
        assertEquals(10.0, rezultat.get().getPret());
    }

    @Test
    void testStergeProdusInexistentAruncaExceptie() {
        when(produsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            produsService.stergeProdus(999L);
        });

        verify(produsRepository, never()).delete(any(Produs.class));
    }

    @Test
    void testGetAllProduse() {
        when(produsRepository.findAll()).thenReturn(List.of(new Produs()));

        assertEquals(1, produsService.getAllProduse().size());
    }

    @Test
    void testStergeProdusExistent() {
        Produs produs = new Produs();
        produs.setId(1L);
        when(produsRepository.findById(1L)).thenReturn(Optional.of(produs));
        when(comandaRepository.existsByProduse_Id(1L)).thenReturn(false);

        produsService.stergeProdus(1L);

        verify(produsRepository).delete(produs);
    }

    @Test
    void testStergeProdusFolositInComandaAruncaExceptie() {
        Produs produs = new Produs();
        produs.setId(1L);
        when(produsRepository.findById(1L)).thenReturn(Optional.of(produs));
        when(comandaRepository.existsByProduse_Id(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> produsService.stergeProdus(1L));

        verify(produsRepository, never()).delete(any(Produs.class));
    }

    @Test
    void testGetProdusDupaIdAruncaExceptieCandNuExista() {
        when(produsRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> produsService.getProdusDupaId(55L));
    }

    @Test
    void testPaginareFaraFiltre() {
        when(produsRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new Produs())));

        assertEquals(1, produsService.getProdusePaginate("", null, 1, 5, "nume", "asc").getTotalElements());
    }

    @Test
    void testPaginareCuCautare() {
        when(produsRepository.findByNumeContainingIgnoreCase(eq("espresso"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Produs())));

        assertEquals(1, produsService.getProdusePaginate("espresso", null, 1, 5, "pret", "desc").getTotalElements());
    }

    @Test
    void testPaginareCuCategorie() {
        when(produsRepository.findByCategorieId(eq(2L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Produs())));

        assertEquals(1, produsService.getProdusePaginate("", 2L, 1, 5, "id", "asc").getTotalElements());
    }

    @Test
    void testPaginareCuCautareSiCategorie() {
        when(produsRepository.findByNumeContainingIgnoreCaseAndCategorieId(eq("latte"), eq(2L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Produs())));

        assertEquals(1, produsService.getProdusePaginate("latte", 2L, 1, 5, "nume", "asc").getTotalElements());
    }
}
