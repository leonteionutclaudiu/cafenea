package com.example.cafenea.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalii_comenzi")
public class DetaliiComanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(name = "produs_id")
    private Produs produs;

    private Integer cantitate;
    private Double pretSalvat; // Prețul produsului în momentul în care s-a emis comanda

    public DetaliiComanda() {}

    public DetaliiComanda(Comanda comanda, Produs produs, Integer cantitate) {
        this.comanda = comanda;
        this.produs = produs;
        this.cantitate = cantitate;
        this.pretSalvat = produs.getPret(); // Copiem prețul actual al produsului
    }

    // Getters și Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Comanda getComanda() { return comanda; }
    public void setComanda(Comanda comanda) { this.comanda = comanda; }

    public Produs getProdus() { return produs; }
    public void setProdus(Produs produs) { this.produs = produs; }

    public Integer getCantitate() { return cantitate; }
    public void setCantitate(Integer cantitate) { this.cantitate = cantitate; }

    public Double getPretSalvat() { return pretSalvat; }
    public void setPretSalvat(Double pretSalvat) { this.pretSalvat = pretSalvat; }

    public Double getSubtotal() {
        return this.pretSalvat * this.cantitate;
    }
}