package com.example.cafenea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**").permitAll() // Oricine poate accesa pagina de login
                        .requestMatchers("/produse/sterge/**").hasRole("ADMIN") // DOAR Managerul (ADMIN) are voie să șteargă produse!
                        .anyRequest().authenticated() // Pentru orice altă pagină (vizualizare, adăugare) trebuie să fii logat
                )
                .formLogin(form -> form
                        .loginPage("/login") // Pagina noastră personalizată de login (o vom crea imediat în HTML)
                        .defaultSuccessUrl("/produse", true) // Unde ne trimite site-ul după ce ne logăm cu succes
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Unde ne trimite după ce dăm Logout
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Algoritmul obligatoriu de criptare a parolelor cerut în proiect (BCrypt)
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Creăm doi utilizatori de test direct în memorie, ca să fie extrem de simplu la prezentarea live
        UserDetails ospatar = User.withUsername("ospatar")
                .password(encoder.encode("cafea123")) // Parola va fi salvată criptat
                .roles("USER") // Rol simplu de angajat
                .build();

        UserDetails manager = User.withUsername("manager")
                .password(encoder.encode("manager123"))
                .roles("ADMIN") // Rol de administrator (Manager)
                .build();

        return new InMemoryUserDetailsManager(ospatar, manager);
    }
}