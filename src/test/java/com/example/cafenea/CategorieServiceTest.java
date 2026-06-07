package com.example.cafenea;

import com.example.cafenea.model.CategorieProdus;
import com.example.cafenea.repository.CategorieProdusRepository;
import com.example.cafenea.repository.ProdusRepository;
import com.example.cafenea.service.CategorieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieProdusRepository categorieProdusRepository;

    @Mock
    private ProdusRepository produsRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    void getAllCategoriiReturneazaLista() {
        when(categorieProdusRepository.findAll()).thenReturn(List.of(new CategorieProdus()));

        assertEquals(1, categorieService.getAllCategorii().size());
    }

    @Test
    void getCategorieByIdReturneazaCategoria() {
        CategorieProdus categorie = new CategorieProdus();
        categorie.setId(1L);
        categorie.setDenumire("Cafea");
        when(categorieProdusRepository.findById(1L)).thenReturn(Optional.of(categorie));

        assertEquals("Cafea", categorieService.getCategorieById(1L).getDenumire());
    }

    @Test
    void getCategorieByIdAruncaExceptieCandNuExista() {
        when(categorieProdusRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categorieService.getCategorieById(2L));
    }

    @Test
    void salveazaCategorieTrimiteLaRepository() {
        CategorieProdus categorie = new CategorieProdus();
        categorie.setDenumire("Ceai");

        categorieService.salveazaCategorie(categorie);

        verify(categorieProdusRepository).save(categorie);
    }

    @Test
    void stergeCategorieExistenta() {
        when(categorieProdusRepository.existsById(1L)).thenReturn(true);
        when(produsRepository.existsByCategorie_Id(1L)).thenReturn(false);

        categorieService.stergeCategorie(1L);

        verify(categorieProdusRepository).deleteById(1L);
    }

    @Test
    void stergeCategorieInexistentaAruncaExceptie() {
        when(categorieProdusRepository.existsById(9L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> categorieService.stergeCategorie(9L));
        verify(categorieProdusRepository, never()).deleteById(9L);
    }

    @Test
    void stergeCategorieCuProduseAsociateAruncaExceptie() {
        when(categorieProdusRepository.existsById(1L)).thenReturn(true);
        when(produsRepository.existsByCategorie_Id(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categorieService.stergeCategorie(1L));

        verify(categorieProdusRepository, never()).deleteById(1L);
    }
}
