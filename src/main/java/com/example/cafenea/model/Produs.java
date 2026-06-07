package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
    @ToString.Exclude
    private List<Comanda> comenzi;

    @ManyToMany
    @JoinTable(
            name = "produs_ingrediente",
            joinColumns = @JoinColumn(name = "produs_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @ToString.Exclude
    private List<Ingredient> ingrediente = new ArrayList<>();
}
