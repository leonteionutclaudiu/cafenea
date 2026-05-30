package com.example.cafenea.config;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
public class SecurityConfig {

    // 1. Serviciul de utilizatori care interoghează tabela ta din PostgreSQL
    @Bean
    public UserDetailsService userDetailsService(UtilizatorRepository utilizatorRepository) {
        return username -> utilizatorRepository.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .roles(u.getRol()) // Adaugă prefixul "ROLE_" automat
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilizatorul cu username-ul " + username + " nu există!"));
    }

    // 2. PasswordEncoder-ul unic bazat pe algoritmul BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 3. REPARAT: Transmitem userDetailsService direct în constructorul Providerului
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // 4. Lanțul principal de filtre de securitate HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(csrf -> {}) // CSRF activ cu regulile implicite

                .authenticationProvider(authenticationProvider)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register").hasRole("ADMIN")
                        .requestMatchers("/categorii/**").authenticated() // Oricine este logat poate gestiona categoriile
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/produse", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .rememberMe(remember -> remember
                        .userDetailsService(userDetailsService)
                        .key("CheieSecretaCafenea2026")
                        .tokenValiditySeconds(1209600) // 14 zile
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );

        return http.build();
    }

    // 5. Inițializatorul automat de date (Data Seeding) modificat
    @Bean
    public CommandLineRunner initDatabase(com.example.cafenea.repository.UtilizatorRepository utilizatorRepository,
                                          com.example.cafenea.repository.CategorieProdusRepository categorieProdusRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Curățare și re-generare utilizator manager
            utilizatorRepository.deleteAll();
            System.out.println("⚠️ Sistem de Securitate: Generăm contul curat de Manager...");
            com.example.cafenea.model.Utilizator managerImplicit = new com.example.cafenea.model.Utilizator();
            managerImplicit.setUsername("manager");
            managerImplicit.setPassword(passwordEncoder.encode("manager123"));
            managerImplicit.setRol("ADMIN");
            utilizatorRepository.save(managerImplicit);
            System.out.println("✅ Succes: Cont inițial pregătit!");

            // 2. NOU: Generare categorii implicite dacă tabela e goală
            if (categorieProdusRepository.count() == 0) {
                System.out.println("⚠️ Bază de date: Tabela de categorii e goală. Generăm categorii implicite...");

                com.example.cafenea.model.CategorieProdus c1 = new com.example.cafenea.model.CategorieProdus();
                c1.setDenumire("Cafea & Specialități");
                categorieProdusRepository.save(c1);

                com.example.cafenea.model.CategorieProdus c2 = new com.example.cafenea.model.CategorieProdus();
                c2.setDenumire("Băuturi Răcoritoare");
                categorieProdusRepository.save(c2);

                com.example.cafenea.model.CategorieProdus c3 = new com.example.cafenea.model.CategorieProdus();
                c3.setDenumire("Patiserie & Deserturi");
                categorieProdusRepository.save(c3);

                System.out.println("✅ Succes: S-au generat 3 categorii de produse în baza de date!");
            }
        };
    }
}