package com.project.deliveryms.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BordereauExpedition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateGeneration;

    private String cheminFichierPdf;

    @OneToOne
    private Colis colis;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public String getCheminFichierPdf() {
        return cheminFichierPdf;
    }

    public void setCheminFichierPdf(String cheminFichierPdf) {
        this.cheminFichierPdf = cheminFichierPdf;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }
}
