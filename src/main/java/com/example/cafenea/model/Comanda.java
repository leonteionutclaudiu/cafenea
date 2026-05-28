package com.example.cafenea.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "comenzi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numarMasa;
    private String statusComanda; // ex: In preparare, Servita

    // Relație ManyToMany: O comandă are mai multe produse, un produs poate fi în mai multe comenzi
    @ManyToMany
    @JoinTable(
            name = "comenzi_produse",
            joinColumns = @JoinColumn(name = "comanda_id"),
            inverseJoinColumns = @JoinColumn(name = "produs_id")
    )
    private List<Produs> produse;
}