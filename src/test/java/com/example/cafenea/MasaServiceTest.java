package com.example.cafenea;

import com.example.cafenea.model.Masa;
import com.example.cafenea.repository.MasaRepository;
import com.example.cafenea.service.MasaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasaServiceTest {

    @Mock
    private MasaRepository masaRepository;

    @InjectMocks
    private MasaService masaService;

    @Test
    void salveazaMasaAruncaExceptiePentruNumarDuplicat() {
        Masa existenta = new Masa();
        existenta.setId(1L);
        existenta.setNumarMasa(5);
        existenta.setStatus("LIBERA");

        Masa noua = new Masa();
        noua.setNumarMasa(5);
        noua.setStatus("LIBERA");

        when(masaRepository.findByNumarMasa(5)).thenReturn(Optional.of(existenta));

        assertThrows(IllegalArgumentException.class, () -> masaService.salveazaMasa(noua));
        verify(masaRepository, never()).save(noua);
    }

    @Test
    void stergeMasaOcupataAruncaExceptie() {
        Masa masa = new Masa();
        masa.setId(1L);
        masa.setNumarMasa(3);
        masa.setStatus("OCUPATA");

        when(masaRepository.findById(1L)).thenReturn(Optional.of(masa));

        assertThrows(IllegalArgumentException.class, () -> masaService.stergeMasa(1L));
        verify(masaRepository, never()).deleteById(1L);
    }

    @Test
    void salveazaMasaNouaFaraDuplicat() {
        Masa masa = new Masa();
        masa.setNumarMasa(8);
        masa.setStatus("LIBERA");

        when(masaRepository.findByNumarMasa(8)).thenReturn(Optional.empty());

        masaService.salveazaMasa(masa);

        verify(masaRepository).save(masa);
    }

    @Test
    void stergeMasaLibera() {
        Masa masa = new Masa();
        masa.setId(2L);
        masa.setNumarMasa(4);
        masa.setStatus("LIBERA");

        when(masaRepository.findById(2L)).thenReturn(Optional.of(masa));

        masaService.stergeMasa(2L);

        verify(masaRepository).deleteById(2L);
    }
}
