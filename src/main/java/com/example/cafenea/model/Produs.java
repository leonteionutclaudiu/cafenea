package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Denumirea produsului nu poate fi goala!")
    private String nume;

    @DecimalMin(value = "1.0", message = "Pretul trebuie sa fie de minimum 1 RON!")
    private double pret;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @NotNull(message = "Va rugam sa selectati o categorie!")
    @ToString.Exclude
    private CategorieProdus categorie;

    @ManyToMany(mappedBy = "produse")
    private List<Comanda> comenzi;
}
