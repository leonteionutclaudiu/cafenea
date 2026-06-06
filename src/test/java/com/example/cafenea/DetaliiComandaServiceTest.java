package com.example.cafenea;

import com.example.cafenea.model.DetaliiComanda;
import com.example.cafenea.model.Produs;
import com.example.cafenea.repository.DetaliiComandaRepository;
import com.example.cafenea.service.DetaliiComandaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetaliiComandaServiceTest {

    @Mock
    private DetaliiComandaRepository detaliiComandaRepository;

    @InjectMocks
    private DetaliiComandaService detaliiComandaService;

    @Test
    void salveazaDetaliuCompleteazaPretulDinProdus() {
        Produs produs = new Produs();
        produs.setNume("Latte");
        produs.setPret(16.5);

        DetaliiComanda detaliu = new DetaliiComanda();
        detaliu.setProdus(produs);
        detaliu.setCantitate(2);

        detaliiComandaService.salveazaDetaliu(detaliu);

        assertEquals(16.5, detaliu.getPretSalvat());
        verify(detaliiComandaRepository).save(detaliu);
    }

    @Test
    void getDetaliuByIdReturneazaDetaliul() {
        DetaliiComanda detaliu = new DetaliiComanda();
        when(detaliiComandaRepository.findById(1L)).thenReturn(Optional.of(detaliu));

        assertEquals(detaliu, detaliiComandaService.getDetaliuById(1L));
    }

    @Test
    void getAllDetaliiReturneazaLista() {
        when(detaliiComandaRepository.findAll()).thenReturn(List.of(new DetaliiComanda()));

        assertEquals(1, detaliiComandaService.getAllDetalii().size());
    }

    @Test
    void stergeDetaliuInexistentAruncaExceptie() {
        when(detaliiComandaRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> detaliiComandaService.stergeDetaliu(99L));
    }

    @Test
    void stergeDetaliuExistent() {
        when(detaliiComandaRepository.existsById(1L)).thenReturn(true);

        detaliiComandaService.stergeDetaliu(1L);

        verify(detaliiComandaRepository).deleteById(1L);
    }
}
