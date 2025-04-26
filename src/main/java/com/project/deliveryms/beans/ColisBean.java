package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.services.ColisService;
import com.project.deliveryms.services.AdresseService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Named
@ViewScoped
public class ColisBean implements Serializable {
    private Colis nouveauColis;

    // Temporary form fields (could also bind directly to nouveauColis properties)
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;

    @Inject
    private ColisService colisService;

    @Inject
    private AdresseService adresseService;

    @PostConstruct
    public void init() {
        nouveauColis = new Colis();
    }

    public String ajouterColis() {
        // Create address first
        var adresse = adresseService.createAdresse(rue, ville, codePostal, pays);

        // Configure the package
        nouveauColis.setAdresseDestinataire(adresse);
        nouveauColis.setStatus(StatusColis.EN_ATTENTE);
        nouveauColis.setNumeroSuivi(UUID.randomUUID().toString());
        nouveauColis.setDateEnvoi(LocalDateTime.now());

        // In a real app, you would select a livreur from a list
        //Livreur livreur = new Livreur(); // Should be fetched from database
        //nouveauColis.setLivreur(livreur);

        // Save the package
        colisService.createColis(nouveauColis);

        // Reset form
        nouveauColis = new Colis();
        rue = ville = codePostal = pays = null;

        return "success?faces-redirect=true"; // Navigation outcome
    }

    // Getters and setters
    public Colis getNouveauColis() { return nouveauColis; }
    public void setNouveauColis(Colis nouveauColis) { this.nouveauColis = nouveauColis; }

    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}