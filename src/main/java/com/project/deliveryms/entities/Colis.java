package com.project.deliveryms.entities;

import com.project.deliveryms.enums.StatusColis;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroSuivi;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id") // ou le nom de votre colonne FK
    private Utilisateur utilisateur;


    private String description;
    private double poids;

    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLivraison;

    @ManyToOne
    private Adresse adresseDestinataire;

    @Enumerated(EnumType.STRING)
    private StatusColis status;

    private boolean deleted;

    @ManyToOne
    private Livreur livreur;

    @OneToOne(mappedBy = "colis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BordereauExpedition bordereauExpedition;

    public BordereauExpedition getBordereauExpedition() {
        return bordereauExpedition;
    }

    public void setBordereauExpedition(BordereauExpedition bordereauExpedition) {
        this.bordereauExpedition = bordereauExpedition;
    }

    // Getters et setters


    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public boolean getDeleted() {
        return deleted;
    }





    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNumeroSuivi() {
        return numeroSuivi;
    }
    public void setNumeroSuivi(String numeroSuivi) {
        this.numeroSuivi = numeroSuivi;
    }
    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }
    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
    public LocalDateTime getDateLivraison() {
        return dateLivraison;
    }
    public void setDateLivraison(LocalDateTime dateLivraison) {
        this.dateLivraison = dateLivraison;
    }
    public Adresse getAdresseDestinataire() {
        return adresseDestinataire;
    }
    public void setAdresseDestinataire(Adresse adresse) {
        this.adresseDestinataire = adresse;
    }
    public Livreur getLivreur() {
        return livreur;
    }
    public void setLivreur(Livreur livreur) {
        this.livreur = livreur;
    }

    public StatusColis getStatus() {
        return status;
    }

    public void setStatus(StatusColis status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids =poids;
}

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public void setDeleted(boolean b) {
    }
    public String getDateEnvoiFormatted() {
        if (dateEnvoi == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateEnvoi.format(formatter);
    }
}
