package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "mese")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Masa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Numarul mesei este obligatoriu.")
    @Min(value = 1, message = "Numarul mesei trebuie sa fie cel putin 1.")
    @Max(value = 200, message = "Numarul mesei nu poate depasi 200.")
    private Integer numarMasa;

    @NotBlank(message = "Statusul mesei este obligatoriu.")
    @Pattern(regexp = "LIBERA|OCUPATA", message = "Statusul mesei trebuie sa fie LIBERA sau OCUPATA.")
    private String status;
}
