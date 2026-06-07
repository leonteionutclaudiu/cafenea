package com.example.cafenea.config;

import com.example.cafenea.model.Masa;
import com.example.cafenea.model.Utilizator;
import com.example.cafenea.repository.MasaRepository;
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
    private MasaRepository masaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (utilizatorRepository.count() == 0) {
            Utilizator managerImplicit = new Utilizator();
            managerImplicit.setUsername("manager");
            managerImplicit.setPassword(passwordEncoder.encode("manager123"));
            managerImplicit.setRol("ADMIN");
            utilizatorRepository.save(managerImplicit);
            System.out.println("✅ Manager generat.");
        }

        if (masaRepository.count() == 0) {
            System.out.println("🪑 Generăm mesele cafenelei...");
            for (int i = 1; i <= 10; i++) {
                Masa masa = new Masa();
                masa.setNumarMasa(i);
                masa.setStatus("LIBERA");
                masaRepository.save(masa);
            }
            System.out.println("✅ 10 mese generate cu succes!");
        }
    }
}
