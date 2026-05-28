package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "produse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validare Server-side obligatorie conform cerinței 5 din proiect
    @NotBlank(message = "Denumirea produsului nu poate fi goală!")
    private String nume;

    @Min(value = 1, message = "Prețul trebuie să fie de minimum 1 RON!")
    private double pret;

    // Un produs aparține unei singure categorii (@ManyToOne)
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CategorieProdus categorie;

    @ManyToMany(mappedBy = "produse")
    private List<Comanda> comenzi;
}