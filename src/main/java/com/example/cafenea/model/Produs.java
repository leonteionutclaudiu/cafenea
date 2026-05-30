package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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

    @NotBlank(message = "Denumirea produsului nu poate fi goală!")
    private String nume;

    @Min(value = 1, message = "Prețul trebuie să fie de minimum 1 RON!")
    private double pret;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @NotNull(message = "Vă rugăm să selectați o categorie!")
    @ToString.Exclude
    private CategorieProdus categorie;

    @ManyToMany(mappedBy = "produse")
    private List<Comanda> comenzi;

}