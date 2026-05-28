package com.example.cafenea.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carduri_fidelitate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardFidelitate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codCard;
    private int puncteAcumulate;

    @OneToOne
    @JoinColumn(name = "utilizator_id")
    private Utilizator utilizator;
}