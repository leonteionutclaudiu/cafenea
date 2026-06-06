package com.example.cafenea;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Test
    void loadUserByUsernameReturneazaUserDetails() {
        UtilizatorRepository repository = mock(UtilizatorRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        Utilizator utilizator = new Utilizator();
        utilizator.setUsername("manager");
        utilizator.setPassword("hash");
        utilizator.setRol("ADMIN");

        when(repository.findByUsername("manager")).thenReturn(Optional.of(utilizator));

        assertEquals("manager", service.loadUserByUsername("manager").getUsername());
    }

    @Test
    void loadUserByUsernameAruncaExceptieCandNuExista() {
        UtilizatorRepository repository = mock(UtilizatorRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        when(repository.findByUsername("nimeni")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nimeni"));
    }
}
