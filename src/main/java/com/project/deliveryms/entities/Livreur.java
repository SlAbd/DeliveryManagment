package com.project.deliveryms.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "livreur")
public class Livreur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Double latitude;
    private Double longitude;
    private String disponibiliter;

    private boolean disponible;

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @OneToOne
    @JoinColumn(name = "user_id") // clé étrangère dans livreur vers user
    private Utilisateur user;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDisponibiliter() { return disponibiliter; }
    public void setDisponibiliter(String disponibiliter) { this.disponibiliter = disponibiliter; }

    public Utilisateur getUtilisateur() { return user; }
    public void setUtilisateur(Utilisateur user) { this.user = user; }
}
