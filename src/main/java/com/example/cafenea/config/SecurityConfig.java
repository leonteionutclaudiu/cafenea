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
                // Dezactivăm protecția CSRF momentan pentru a nu bloca formularele de test
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitem accesul liber la pagina de login, la erori și la resurse statice
                        .requestMatchers("/login", "/login?error", "/login?logout", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/produse/sterge/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/produse", true)
                        .permitAll() // Îi dă dreptul lui Spring Security să lase această rută liberă
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
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