package com.example.cafenea.config;

import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.UtilizatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UtilizatorRepository utilizatorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Ne asigurăm din cod că curățăm tabelele dacă au rămas date inconsistente la teste
        // Dacă vrei să fii sigur, poți lăsa linia de mai jos pornită o singură rulare:
        // utilizatorRepository.deleteAll();

        if (utilizatorRepository.count() == 0) {
            System.out.println("⚠️ Generăm contul curat de Manager în PostgreSQL...");

            Utilizator managerImplicit = new Utilizator();
            managerImplicit.setUsername("manager");
            // Criptare explicită cu BCrypt
            managerImplicit.setPassword(passwordEncoder.encode("manager123"));
            // Salvăm simplu "ADMIN" - metoda .roles() din SecurityConfig se va ocupa de mapare
            managerImplicit.setRol("ADMIN");

            utilizatorRepository.save(managerImplicit);
            System.out.println("✅ Cont generat cu succes! User: manager | Parolă: manager123");
        }
    }
}