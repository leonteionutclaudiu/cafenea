package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "utilizatori")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilizator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username-ul este obligatoriu.")
    @Size(min = 3, max = 50, message = "Username-ul trebuie sa aiba intre 3 si 50 de caractere.")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Parola este obligatorie.")
    @Size(min = 6, message = "Parola trebuie sa aiba minimum 6 caractere.")
    private String password;

    @NotBlank(message = "Rolul este obligatoriu.")
    @Pattern(regexp = "ADMIN|USER|MANAGER", message = "Rolul trebuie sa fie ADMIN, USER sau MANAGER.")
    private String rol;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profil_id", referencedColumnName = "id")
    private ProfilUtilizator profil;
}
