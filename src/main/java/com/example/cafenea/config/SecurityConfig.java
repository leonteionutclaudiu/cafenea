package com.example.cafenea.config;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
                        .roles(u.getRol())
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider,
                                                   UserDetailsService userDetailsService) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produse", "/categorii", "/ingrediente").authenticated()
                        .requestMatchers("/register", "/mese/**").hasRole("ADMIN")
                        .requestMatchers("/categorii/salveaza", "/categorii/editeaza/**", "/categorii/sterge/**").hasRole("ADMIN")
                        .requestMatchers("/produse/nou", "/produse/editeaza/**", "/produse/sterge/**", "/produse/salveaza").hasRole("ADMIN")
                        .requestMatchers("/ingrediente/nou", "/ingrediente/editeaza/**", "/ingrediente/sterge/**", "/ingrediente/salveaza").hasRole("ADMIN")
                        .requestMatchers("/comenzi/sterge/**").hasRole("ADMIN")
                        .requestMatchers("/utilizatori/sterge/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
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
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("cafenea-remember-me-key")
                        .tokenValiditySeconds(14 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService)
                );

        return http.build();
    }

    @Bean
    public CommandLineRunner initDatabase(UtilizatorRepository utilizatorRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (utilizatorRepository.findByUsername("manager").isEmpty()) {
                Utilizator manager = new Utilizator();
                manager.setUsername("manager");
                manager.setPassword(passwordEncoder.encode("manager123"));
                manager.setRol("ADMIN");
                utilizatorRepository.save(manager);
            }
        };
    }
}
