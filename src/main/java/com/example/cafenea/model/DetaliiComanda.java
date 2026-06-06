package com.example.cafenea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "detalii_comenzi")
public class DetaliiComanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comanda_id")
    @NotNull(message = "Comanda este obligatorie.")
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(name = "produs_id")
    @NotNull(message = "Produsul este obligatoriu.")
    private Produs produs;

    @NotNull(message = "Cantitatea este obligatorie.")
    @Min(value = 1, message = "Cantitatea trebuie sa fie cel putin 1.")
    private Integer cantitate;

    @NotNull(message = "Pretul salvat este obligatoriu.")
    @DecimalMin(value = "0.0", message = "Pretul salvat nu poate fi negativ.")
    private Double pretSalvat;

    public DetaliiComanda() {}

    public DetaliiComanda(Comanda comanda, Produs produs, Integer cantitate) {
        this.comanda = comanda;
        this.produs = produs;
        this.cantitate = cantitate;
        this.pretSalvat = produs.getPret();
    }

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
