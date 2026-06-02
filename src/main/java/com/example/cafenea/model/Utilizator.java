package com.example.cafenea.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "utilizatori")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilizator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String rol; // ADMIN (Manager) sau USER (Ospătar)

}