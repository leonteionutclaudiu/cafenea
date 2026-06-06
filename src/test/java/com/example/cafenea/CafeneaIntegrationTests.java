package com.example.cafenea;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CafeneaIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

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
    void adminPoateAccesaDetaliiComenzi() throws Exception {
        mockMvc.perform(get("/detalii-comenzi"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", roles = "ADMIN")
    void rutaInexistentaIntoarce404() throws Exception {
        mockMvc.perform(get("/mese/adauga"))
                .andExpect(status().isNotFound());
    }
}
