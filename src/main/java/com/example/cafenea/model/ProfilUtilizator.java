package com.example.cafenea.model;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profil_utilizator")
@Data
@NoArgsConstructor
public class ProfilUtilizator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele complet este obligatoriu")
    @Size(min = 3, max = 100, message = "Numele trebuie să aibă între 3 și 100 caractere")
    private String numeComplet;

    @Size(max = 255, message = "Adresa este prea lungă")
    private String adresa;

    @Pattern(regexp = "^(07[0-9]{8})?$", message = "Numărul de telefon trebuie să înceapă cu 07 și să aibă 10 cifre")
    private String telefon;

    @OneToOne(mappedBy = "profil")
    private Utilizator utilizator;
}