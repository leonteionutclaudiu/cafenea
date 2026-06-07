package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categorii_produs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieProdus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Denumirea categoriei este obligatorie!")
    @Size(min = 3, message = "Denumirea trebuie să aibă cel puțin 3 caractere!")
    private String denumire;

    @OneToMany(mappedBy = "categorie")
    @ToString.Exclude
    private List<Produs> produse;
}
