package com.example.cafenea.model;

import jakarta.persistence.*;
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
    private Integer numarMasa;

    private String status; // Ex: LIBERA, OCUPATA
}