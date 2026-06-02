package com.example.cafenea.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comenzi")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataComanda;
    private Double totalPlata;
    private String status; // PRIMITA, IN_PREPARARE, FINALIZATA

    @Column(name = "numar_masa", nullable = true)
    private Integer numarMasa;

    @ManyToOne
    @JoinColumn(name = "utilizator_id")
    private Utilizator utilizator; // Acesta reprezintă OSPĂTARUL

    @ManyToMany
    @JoinTable(
            name = "comanda_produse",
            joinColumns = @JoinColumn(name = "comanda_id"),
            inverseJoinColumns = @JoinColumn(name = "produs_id")
    )
    private List<Produs> produse = new ArrayList<>();

    public Comanda() {
        this.dataComanda = LocalDateTime.now();
        this.status = "PRIMITA";
        this.totalPlata = 0.0;
        this.numarMasa = null;
    }

    @ManyToOne
    @JoinColumn(name = "masa_id")
    private Masa masa;
    
    // Getters și Setters
    public Masa getMasa() { return masa; }
    public void setMasa(Masa masa) { this.masa = masa; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataComanda() { return dataComanda; }
    public void setDataComanda(LocalDateTime dataComanda) { this.dataComanda = dataComanda; }

    public Double getTotalPlata() { return totalPlata; }
    public void setTotalPlata(Double totalPlata) { this.totalPlata = totalPlata; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Utilizator getUtilizator() { return utilizator; }
    public void setUtilizator(Utilizator utilizator) { this.utilizator = utilizator; }
    public List<Produs> getProduse() { return produse; }
    public void setProduse(List<Produs> produse) { this.produse = produse; }

    public Integer getNumarMasa() { return numarMasa; }
    public void setNumarMasa(Integer numarMasa) { this.numarMasa = numarMasa; }

    public void calculeazaTotal() {
        if (produse != null) {
            this.totalPlata = produse.stream().mapToDouble(Produs::getPret).sum();
        } else {
            this.totalPlata = 0.0;
        }
    }

    public java.util.Map<Produs, Long> getProduseGrupateCuCantitate() {
        if (this.produse == null) {
            return new java.util.HashMap<>();
        }
        return this.produse.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p,
                        java.util.stream.Collectors.counting()
                ));
    }

    public int getCantitateProdus(Long produsId) {
        if (this.produse == null) {
            return 0;
        }
        int count = 0;
        for (Produs p : this.produse) {
            if (p.getId().equals(produsId)) {
                count++;
            }
        }
        return count;
    }
}