package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.services.ColisService;
import com.project.deliveryms.services.LivreurService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LivreurColisBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(LivreurColisBean.class.getName());

    private List<Livreur> livreursIndisponibles;
    private List<Colis> colisNonAffectes;

    private Long idLivreurSelectionne;
    private Long idColisAffectation;

    // Informations du livreur sélectionné pour affichage
    private Livreur livreurSelectionne;

    @Inject
    private LivreurService livreurService;

    @Inject
    private ColisService colisService;

    @PostConstruct
    public void init() {
        LOG.info("Initialisation du LivreurColisBean");
        rafraichirDonnees();
    }

    private void rafraichirDonnees() {
        try {
            livreursIndisponibles = livreurService.getLivreursIndisponibles();
            colisNonAffectes = colisService.getColisNonAffectes();

            LOG.info("Données rafraîchies - Livreurs disponibles: " +
                    (livreursIndisponibles != null ? livreursIndisponibles.size() : 0) +
                    ", Colis non affectés: " +
                    (colisNonAffectes != null ? colisNonAffectes.size() : 0));
        } catch (Exception e) {
            LOG.severe("Erreur lors du rafraîchissement des données: " + e.getMessage());
            addErrorMessage("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    public void preparerAffectation(Long livreurId) {
        try {
            LOG.info("Préparation d'affectation pour le livreur ID: " + livreurId);

            idLivreurSelectionne = livreurId;
            livreurSelectionne = livreurService.getLivreurById(livreurId);

            if (livreurSelectionne == null) {
                addErrorMessage("Livreur introuvable");
                return;
            }

            // Vérifier que le livreur est disponible
            if (!"oui".equals(livreurSelectionne.getDisponibiliter())) {
                addErrorMessage("Ce livreur n'est pas disponible pour une affectation");
                return;
            }

            // Réinitialiser la sélection de colis
            idColisAffectation = null;

            // Rafraîchir la liste des colis disponibles
            colisNonAffectes = colisService.getColisNonAffectes();

            LOG.info("Préparation terminée pour: " + livreurSelectionne.getUtilisateur().getNom() +
                    " " + livreurSelectionne.getUtilisateur().getPrenom());

        } catch (Exception e) {
            LOG.severe("Erreur lors de la préparation d'affectation: " + e.getMessage());
            addErrorMessage("Erreur lors de la préparation: " + e.getMessage());
        }
    }

    public String affecterColisAuLivreur() {
        try {
            LOG.info("--- Début Affectation ---");
            LOG.info("Livreur ID: " + idLivreurSelectionne);
            LOG.info("Colis ID: " + idColisAffectation);

            if (idLivreurSelectionne == null) {
                addErrorMessage("Aucun livreur sélectionné");
                return "";
            }

            if (idColisAffectation == null) {
                addErrorMessage("Veuillez sélectionner un colis à affecter");
                return "";
            }

            // Effectuer l'affectation
            colisService.affecterColis(idColisAffectation, idLivreurSelectionne);

            // Récupérer les informations pour le message de succès
            Colis colis = colisService.findById(idColisAffectation);
            Livreur livreur = livreurService.getLivreurById(idLivreurSelectionne);

            String messageSucces = "Colis " + colis.getNumeroSuivi() +
                    " affecté avec succès au livreur " +
                    livreur.getUtilisateur().getNom() + " " +
                    livreur.getUtilisateur().getPrenom();

            addMessage(messageSucces);
            LOG.info("Affectation réussie: " + messageSucces);

            // Recharger les listes
            rafraichirDonnees();

            // Réinitialiser les sélections
            idLivreurSelectionne = null;
            idColisAffectation = null;
            livreurSelectionne = null;

        } catch (Exception e) {
            LOG.severe("Erreur lors de l'affectation: " + e.getMessage());
            addErrorMessage("Erreur lors de l'affectation: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    // Méthodes utilitaires pour les messages
    private void addMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    // Getters et Setters
    public List<Livreur> getLivreursIndisponibles() {
        if (livreursIndisponibles == null) {
            rafraichirDonnees();
        }
        return livreursIndisponibles;
    }

    public List<Colis> getColisNonAffectes() {
        if (colisNonAffectes == null) {
            rafraichirDonnees();
        }
        return colisNonAffectes;
    }

    // MÉTHODE MANQUANTE AJOUTÉE
    public int getNombreColisNonAffectes() {
        if (colisNonAffectes == null) {
            rafraichirDonnees();
        }
        return colisNonAffectes != null ? colisNonAffectes.size() : 0;
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

    public Livreur getLivreurSelectionne() {
        return livreurSelectionne;
    }

    public void setLivreurSelectionne(Livreur livreurSelectionne) {
        this.livreurSelectionne = livreurSelectionne;
    }
}
