package com.example.cafenea.service;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizatorRepository utilizatorRepository;

    public CustomUserDetailsService(UtilizatorRepository utilizatorRepository) {
        this.utilizatorRepository = utilizatorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilizator utilizator = utilizatorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizator negăsit: " + username));

        // Transmitem datele către Spring Security. Îi adăugăm manual prefixul "ROLE_"
        return User.builder()
                .username(utilizator.getUsername())
                .password(utilizator.getPassword())
                .roles(utilizator.getRol()) // ADMIN devine ROLE_ADMIN, USER devine ROLE_USER [cite: 83]
                .build();
    }
}
