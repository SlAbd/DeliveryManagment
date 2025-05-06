package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.services.ColisService;
import com.project.deliveryms.services.LivreurService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class LivreurColisBean implements Serializable {

    private List<Livreur> livreursIndisponibles;
    private List<Colis> colisNonAffectes;

    private Long idLivreurSelectionne;
    private Long idColisAffectation;

    @Inject
    private LivreurService livreurService;

    @Inject
    private ColisService colisService;

    @PostConstruct
    public void init() {
        livreursIndisponibles = livreurService.getLivreursIndisponibles();
        colisNonAffectes = colisService.getColisNonAffectes();
    }

    public void affecterColisAuLivreur() {
        try {
            if (idLivreurSelectionne != null && idColisAffectation != null) {
                colisService.affecterColis(idColisAffectation, idLivreurSelectionne);

                // Recharger les listes
                livreursIndisponibles = livreurService.getLivreursIndisponibles();
                colisNonAffectes = colisService.getColisNonAffectes();
            }
        } catch (Exception e) {
            e.printStackTrace(); // À remplacer par une gestion plus élégante (FacesMessage, logger, etc.)
        }
    }

    // Getters et Setters
    public List<Livreur> getLivreursIndisponibles() {
        return livreursIndisponibles;
    }

    public List<Colis> getColisNonAffectes() {
        return colisNonAffectes;
    }

    public Long getIdLivreurSelectionne() {
        return idLivreurSelectionne;
    }

    public void setIdLivreurSelectionne(Long idLivreurSelectionne) {
        this.idLivreurSelectionne = idLivreurSelectionne;
    }

    public Long getIdColisAffectation() {
        return idColisAffectation;
    }

    public void setIdColisAffectation(Long idColisAffectation) {
        this.idColisAffectation = idColisAffectation;
    }
}
