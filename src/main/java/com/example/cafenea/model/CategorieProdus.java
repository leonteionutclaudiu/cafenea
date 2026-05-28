package com.example.cafenea.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categorii_produs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieProdus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String denumire; // ex: Cafea, Bauturi Reci, Patiserie

    // O categorie are mai multe produse
    @OneToMany(mappedBy = "categorie")
    private List<Produs> produse;
}