package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.services.LivreurService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class LivreurBean implements Serializable {

    @Inject
    private LivreurService livreurService;

    private Livreur nouveauLivreur;
    private List<Livreur> livreurs;

    @PostConstruct
    public void init() {
        // Initialisation d'un nouveau livreur pour les formulaires d'ajout
        nouveauLivreur = new Livreur();
        // Charger la liste des livreurs
        rafraichirListeLivreurs();
    }

    // Méthode pour rafraîchir la liste des livreurs
    private void rafraichirListeLivreurs() {
        livreurs = livreurService.getAllLivreurs();
    }

    public Livreur getNouveauLivreur() {
        return nouveauLivreur;
    }

    public void setNouveauLivreur(Livreur nouveauLivreur) {
        this.nouveauLivreur = nouveauLivreur;
    }

    public List<Livreur> getLivreurs() {
        if (livreurs == null) {
            rafraichirListeLivreurs();
        }
        return livreurs;
    }

    public String ajouterLivreur() {
        if (nouveauLivreur != null && nouveauLivreur.getUser() != null) {
            livreurService.createLivreur(
                    nouveauLivreur.getUser().getEmail(),
                    nouveauLivreur.getUser().getNom(),
                    nouveauLivreur.getUser().getPrenom(),
                    nouveauLivreur.getLatitude(),
                    nouveauLivreur.getLongitude(),
                    nouveauLivreur.getDisponibiliter()
            );
            // Réinitialiser le formulaire après l'ajout
            nouveauLivreur = new Livreur();
            // Rafraîchir la liste des livreurs
            rafraichirListeLivreurs();
            addMessage("Livreur ajouté avec succès.");
        }
        return null; // Rester sur la même page
    }

    // Suppression d'un livreur
    public void supprimerLivreur(Long id) {
        livreurService.deleteLivreur(id);
        // Rafraîchir la liste après suppression
        rafraichirListeLivreurs();
        addMessage("Livreur supprimé avec succès.");
    }

    // Mise à jour d'un livreur
    public String modifierLivreur(Livreur livreur) {
        try {
            livreurService.updateLivreur(livreur);
            // Rafraîchir la liste après modification
            rafraichirListeLivreurs();
            addMessage("Livreur modifié avec succès");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erreur lors de la modification: " + e.getMessage(), null));
        }
        return null;
    }

    private void addMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
}