package com.example.cafenea;

import com.example.cafenea.model.Comanda;
import com.example.cafenea.model.Produs;
import com.example.cafenea.repository.ComandaRepository;
import com.example.cafenea.repository.ProdusRepository;
import com.example.cafenea.service.ComandaService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ComandaServiceTest {

    @Mock
    private ComandaRepository comandaRepository;

    @Mock
    private ProdusRepository produsRepository;

    @Test
    void salveazaComandaCalculeazaTotalul() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        Comanda comanda = new Comanda();

        Produs cafea = new Produs();
        cafea.setId(1L);
        cafea.setPret(10.0);

        Produs ceai = new Produs();
        ceai.setId(2L);
        ceai.setPret(8.0);

        when(produsRepository.findById(1L)).thenReturn(Optional.of(cafea));
        when(produsRepository.findById(2L)).thenReturn(Optional.of(ceai));

        service.salveazaComanda(comanda, List.of(1L, 2L));

        assertEquals(18.0, comanda.getTotalPlata());
        verify(comandaRepository).save(comanda);
    }

    @Test
    void schimbaStatusAruncaExceptiePentruComandaInexistenta() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        when(comandaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.schimbaStatus(10L, "FINALIZATA"));
    }

    @Test
    void getComenziPaginateReturneazaPagina() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        when(comandaRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new Comanda())));

        assertEquals(1, service.getComenziPaginate(1, 5, "dataComanda", "asc").getTotalElements());
    }

    @Test
    void getAllComenziReturneazaLista() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        when(comandaRepository.findAll()).thenReturn(List.of(new Comanda()));

        assertEquals(1, service.getAllComenzi().size());
    }

    @Test
    void salveazaComandaAruncaExceptiePentruProdusInexistent() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        when(produsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.salveazaComanda(new Comanda(), List.of(99L)));
    }

    @Test
    void schimbaStatusActualizeazaComanda() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        Comanda comanda = new Comanda();
        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));

        service.schimbaStatus(1L, "FINALIZATA");

        assertEquals("FINALIZATA", comanda.getStatus());
        verify(comandaRepository).save(comanda);
    }

    @Test
    void stergeComandaExistenta() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        when(comandaRepository.existsById(1L)).thenReturn(true);

        service.stergeComanda(1L);

        verify(comandaRepository).deleteById(1L);
    }

    @Test
    void getComandaByIdReturneazaComanda() {
        ComandaService service = new ComandaService(comandaRepository, produsRepository);
        Comanda comanda = new Comanda();
        when(comandaRepository.findById(1L)).thenReturn(Optional.of(comanda));

        assertEquals(comanda, service.getComandaById(1L));
    }
}
