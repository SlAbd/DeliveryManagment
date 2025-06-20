package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.entities.Utilisateur;
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
import java.util.stream.Collectors;

@Named
@ViewScoped
public class LivreurBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LivreurBean.class.getName());

    @Inject
    private LivreurService livreurService;

    private Livreur nouveauLivreur;
    private Livreur livreurModifie;

    private List<Livreur> livreurs;

    @PostConstruct
    public void init() {
        LOG.info("Initialisation du LivreurBean");
        nouveauLivreur = new Livreur();
        nouveauLivreur.setUtilisateur(new Utilisateur());

        livreurModifie = new Livreur();
        livreurModifie.setUtilisateur(new Utilisateur());

        rafraichirListeLivreurs();
    }

    private void rafraichirListeLivreurs() {
        livreurs = livreurService.getAllLivreurs();
        LOG.info("Liste des livreurs rafraîchie : " + livreurs.size() + " livreurs trouvés");
    }

    public List<Livreur> getLivreurs() {
        if (livreurs == null) {
            rafraichirListeLivreurs();
        }
        return livreurs;
    }

    public Livreur getNouveauLivreur() {
        if (nouveauLivreur == null) {
            nouveauLivreur = new Livreur();
            nouveauLivreur.setUtilisateur(new Utilisateur());
        }
        return nouveauLivreur;
    }

    public void setNouveauLivreur(Livreur nouveauLivreur) {
        this.nouveauLivreur = nouveauLivreur;
    }

    public Livreur getLivreurModifie() {
        return livreurModifie;
    }

    public void setLivreurModifie(Livreur livreurModifie) {
        this.livreurModifie = livreurModifie;
    }

    public String ajouterLivreur() {
        try {
            if (nouveauLivreur != null && nouveauLivreur.getUtilisateur() != null) {
                LOG.info("Tentative d'ajout d'un livreur: " + nouveauLivreur.getUtilisateur().getNom() + " " +
                        nouveauLivreur.getUtilisateur().getPrenom());

                livreurService.createLivreur(
                        nouveauLivreur.getUtilisateur().getEmail(),
                        nouveauLivreur.getUtilisateur().getNom(),
                        nouveauLivreur.getUtilisateur().getPrenom(),
                        nouveauLivreur.getLatitude(),
                        nouveauLivreur.getLongitude(),
                        nouveauLivreur.getDisponibiliter()
                );

                nouveauLivreur = new Livreur();
                nouveauLivreur.setUtilisateur(new Utilisateur());

                rafraichirListeLivreurs();
                addMessage("Livreur ajouté avec succès.");
                LOG.info("Livreur ajouté avec succès");
            }
        } catch (Exception e) {
            LOG.severe("Erreur lors de l'ajout du livreur: " + e.getMessage());
            addErrorMessage("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void supprimerLivreur(Long id) {
        try {
            LOG.info("Tentative de suppression du livreur avec ID: " + id);
            livreurService.deleteLivreur(id);
            rafraichirListeLivreurs();
            addMessage("Livreur supprimé avec succès.");
            LOG.info("Livreur supprimé avec succès");
        } catch (Exception e) {
            LOG.severe("Erreur lors de la suppression du livreur: " + e.getMessage());
            addErrorMessage("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    // Méthode améliorée pour la préparation de la modification
    public void preparerModification(Livreur livreur) {
        try {
            LOG.info("Préparation de la modification pour le livreur ID: " + livreur.getId());

            // Récupérer l'entité complète depuis la base de données
            Livreur livreurFromDB = livreurService.getLivreurById(livreur.getId());

            if (livreurFromDB == null) {
                LOG.warning("Livreur introuvable avec ID: " + livreur.getId());
                addErrorMessage("Livreur introuvable (ID: " + livreur.getId() + ")");
                return;
            }

            // Créer une nouvelle instance pour éviter les problèmes de détachement
            livreurModifie = new Livreur();
            livreurModifie.setId(livreurFromDB.getId());
            livreurModifie.setDisponibiliter(livreurFromDB.getDisponibiliter());

            // Copier les coordonnées si elles existent
            if (livreurFromDB.getLatitude() != null) {
                livreurModifie.setLatitude(livreurFromDB.getLatitude());
            }
            if (livreurFromDB.getLongitude() != null) {
                livreurModifie.setLongitude(livreurFromDB.getLongitude());
            }

            // Copier les informations utilisateur
            Utilisateur userClone = new Utilisateur();
            if (livreurFromDB.getUtilisateur() != null) {
                userClone.setId(livreurFromDB.getUtilisateur().getId());
                userClone.setEmail(livreurFromDB.getUtilisateur().getEmail());
                userClone.setNom(livreurFromDB.getUtilisateur().getNom());
                userClone.setPrenom(livreurFromDB.getUtilisateur().getPrenom());
                userClone.setRole(livreurFromDB.getUtilisateur().getRole());
            }

            livreurModifie.setUtilisateur(userClone);

            // Logs pour vérification
            LOG.info("--- Préparation Modification Livreur ---");
            LOG.info("ID: " + livreurModifie.getId());
            LOG.info("Nom: " + livreurModifie.getUtilisateur().getNom());
            LOG.info("Prénom: " + livreurModifie.getUtilisateur().getPrenom());
            LOG.info("Email: " + livreurModifie.getUtilisateur().getEmail());
            LOG.info("Disponibilité: " + livreurModifie.getDisponibiliter());
            LOG.info("-----------------------------------");

            // Force le bean à être synchronisé avec la vue
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("formModification");

        } catch (Exception e) {
            LOG.severe("Erreur lors de la préparation de la modification: " + e.getMessage());
            addErrorMessage("Erreur lors de la préparation de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode améliorée pour la modification du livreur
    public String modifierLivreur() {
        try {
            if (livreurModifie == null || livreurModifie.getId() == null) {
                LOG.warning("Tentative de modification sans livreur sélectionné");
                addErrorMessage("Aucun livreur sélectionné pour modification");
                return null;
            }

            LOG.info("--- Modification Livreur ---");
            LOG.info("ID: " + livreurModifie.getId());
            LOG.info("Nom: " + livreurModifie.getUtilisateur().getNom());
            LOG.info("Prénom: " + livreurModifie.getUtilisateur().getPrenom());
            LOG.info("Email: " + livreurModifie.getUtilisateur().getEmail());
            LOG.info("Disponibilité: " + livreurModifie.getDisponibiliter());
            LOG.info("----------------------------");

            livreurService.updateLivreur(livreurModifie);
            rafraichirListeLivreurs();
            addMessage("Livreur modifié avec succès.");
            LOG.info("Livreur modifié avec succès");

            return null;
        } catch (Exception e) {
            LOG.severe("Erreur lors de la modification: " + e.getMessage());
            addErrorMessage("Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Livreur> getLivreursDisponibles() {
        return livreurs.stream()
                .filter(l -> "oui".equals(l.getDisponibiliter()))
                .collect(Collectors.toList());
    }

    public List<Livreur> getLivreursEnMission() {
        return livreurs.stream()
                .filter(l -> "non".equalsIgnoreCase(l.getDisponibiliter()))
                .collect(Collectors.toList());
    }


    public int getNombreLivreursDisponibles() {
        return (int) livreurs.stream()
                .filter(l -> "oui".equals(l.getDisponibiliter()))
                .count();
    }

    public int getNombreLivreursEnMission() {
        return (int) livreurs.stream()
                .filter(l -> "non".equals(l.getDisponibiliter()))
                .count();
    }
}