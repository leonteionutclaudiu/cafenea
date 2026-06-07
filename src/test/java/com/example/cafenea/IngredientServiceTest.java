package com.example.cafenea;

import com.example.cafenea.model.Ingredient;
import com.example.cafenea.repository.IngredientRepository;
import com.example.cafenea.repository.ProdusRepository;
import com.example.cafenea.service.IngredientService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private ProdusRepository produsRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void getIngredientByIdReturneazaIngredientulCandExista() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setNumeIngredient("Lapte");
        ingredient.setCantitateStoc(10);

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        Ingredient rezultat = ingredientService.getIngredientById(1L);

        assertEquals("Lapte", rezultat.getNumeIngredient());
        assertEquals(10, rezultat.getCantitateStoc());
    }

    @Test
    void deleteIngredientAruncaExceptieCandNuExista() {
        when(ingredientRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> ingredientService.deleteIngredient(99L));
        verify(ingredientRepository, never()).deleteById(99L);
    }

    @Test
    void saveIngredientTrimiteEntitateaLaRepository() {
        Ingredient ingredient = new Ingredient();
        ingredient.setNumeIngredient("Cafea");
        ingredient.setCantitateStoc(20);

        ingredientService.saveIngredient(ingredient);

        verify(ingredientRepository, times(1)).save(ingredient);
    }

    @Test
    void getAllIngredientsReturneazaLista() {
        when(ingredientRepository.findAll()).thenReturn(java.util.List.of(new Ingredient()));

        assertEquals(1, ingredientService.getAllIngredients().size());
    }

    @Test
    void getIngredientePaginateCuKeywordFolosesteCautare() {
        when(ingredientRepository.findByNumeIngredientContainingIgnoreCase(eq("lapte"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of(new Ingredient())));

        assertEquals(1, ingredientService.getIngredientePaginate("lapte", 1, 5, "numeIngredient", "asc").getTotalElements());
    }

    @Test
    void getIngredientePaginateFaraKeywordReturneazaToate() {
        when(ingredientRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of(new Ingredient())));

        assertEquals(1, ingredientService.getIngredientePaginate("", 1, 5, "cantitateStoc", "desc").getTotalElements());
    }

    @Test
    void deleteIngredientExistentSterge() {
        when(ingredientRepository.existsById(1L)).thenReturn(true);
        when(produsRepository.existsByIngrediente_Id(1L)).thenReturn(false);

        ingredientService.deleteIngredient(1L);

        verify(ingredientRepository).deleteById(1L);
    }

    @Test
    void deleteIngredientFolositDeProdusAruncaExceptie() {
        when(ingredientRepository.existsById(1L)).thenReturn(true);
        when(produsRepository.existsByIngrediente_Id(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> ingredientService.deleteIngredient(1L));

        verify(ingredientRepository, never()).deleteById(1L);
    }
}
