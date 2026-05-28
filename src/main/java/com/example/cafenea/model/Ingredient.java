package com.example.cafenea.model;

import jakarta.persistence.*;
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

    private String numeIngredient; // ex: Lapte, Boabe Cafea, Sirop Vanilie
    private int cantitateStoc;
}