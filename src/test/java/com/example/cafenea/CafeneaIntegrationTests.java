package com.example.cafenea;

import com.example.cafenea.model.CategorieProdus;
import com.example.cafenea.model.Comanda;
import com.example.cafenea.model.Produs;
import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.CategorieProdusRepository;
import com.example.cafenea.repository.ComandaRepository;
import com.example.cafenea.repository.ProdusRepository;
import com.example.cafenea.repository.UtilizatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CafeneaIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ProdusRepository produsRepository;

    @Autowired
    private CategorieProdusRepository categorieProdusRepository;

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Test
    void loginPageEstePublica() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void produseNecesitaAutentificare() throws Exception {
        mockMvc.perform(get("/produse"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "manager", roles = "ADMIN")
    void adminPoateAccesaMese() throws Exception {
        mockMvc.perform(get("/mese"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", roles = "ADMIN")
    void comenziSeAfiseazaCandExistaProdusePeComanda() throws Exception {
        CategorieProdus categorie = new CategorieProdus();
        categorie.setDenumire("Cafea test");
        categorie = categorieProdusRepository.save(categorie);

        Produs produs = new Produs();
        produs.setNume("Latte test");
        produs.setPret(15.0);
        produs.setCategorie(categorie);
        produs = produsRepository.save(produs);

        Utilizator utilizator = utilizatorRepository.findByUsername("manager").orElseThrow();

        Comanda comanda = new Comanda();
        comanda.setUtilizator(utilizator);
        comanda.setProduse(List.of(produs, produs));
        comanda.calculeazaTotal();
        comandaRepository.save(comanda);

        mockMvc.perform(get("/comenzi"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", roles = "ADMIN")
    void rutaInexistentaIntoarce404() throws Exception {
        mockMvc.perform(get("/mese/adauga"))
                .andExpect(status().isNotFound());
    }
}
