package com.example.cafenea.config;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import com.example.cafenea.repository.ComandaRepository;
import com.example.cafenea.repository.CategorieProdusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UtilizatorRepository utilizatorRepository) {
        return username -> utilizatorRepository.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRol()) // Spring adaugă prefixul ROLE_ automat
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilizator inexistent: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(csrf -> {})
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register").hasRole("ADMIN")
                        // Permite ștergerea pentru ADMIN sau MANAGER
                        .requestMatchers("/utilizatori/sterge/**").hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/produse", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CommandLineRunner initDatabase(UtilizatorRepository utilizatorRepository,
                                          ComandaRepository comandaRepository,
                                          CategorieProdusRepository categorieProdusRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (utilizatorRepository.findByUsername("manager").isEmpty()) {
                Utilizator m = new Utilizator();
                m.setUsername("manager");
                m.setPassword(passwordEncoder.encode("manager123"));
                m.setRol("ADMIN");
                utilizatorRepository.save(m);
            }
        };
    }
}