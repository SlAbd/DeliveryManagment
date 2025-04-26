package com.project.deliveryms.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HistoriqueSuivi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statut;
    private LocalDateTime date;

    @ManyToOne
    private Colis colis;

    // Getters et setters

    public Long getId() {
        return id;
    }

    public String getStatut() {
        return statut;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Colis getColis() {
        return colis;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }
}

