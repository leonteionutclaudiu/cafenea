package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "ingrediente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele ingredientului este obligatoriu.")
    @Size(min = 2, max = 80, message = "Numele ingredientului trebuie sa aiba intre 2 si 80 de caractere.")
    private String numeIngredient; // ex: Lapte, Boabe Cafea, Sirop Vanilie

    @Min(value = 0, message = "Cantitatea din stoc nu poate fi negativa.")
    private int cantitateStoc;
}
