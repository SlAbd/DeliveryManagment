package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.services.ColisService;
import com.project.deliveryms.services.LivreurService;
import com.project.deliveryms.services.UtilisateurService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LivreurColisBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(LivreurColisBean.class.getName());

    private List<Livreur> livreursIndisponibles;
    private List<Colis> colisNonAffectes;
    private List<Colis> colisLivreur;
    private List<Colis> troisDerniersColis;

    private Livreur livreur;
    private Livreur livreurSelectionne;

    private Long idLivreurSelectionne;
    private Long idColisAffectation;
    private Long idColis;

    private Colis colisDetail;
    private String statusFiltre;

    private int totalColis;
    private int colisEnTransit;
    private long colisTermines;

    private Map<Long, String> nouveauxStatus = new HashMap<>();

    @Inject
    private LivreurService livreurService;

    @Inject
    private ColisService colisService;

    @Inject
    private UtilisateurService utilisateurService;

    @PostConstruct
    public void init() {
        livreursIndisponibles = livreurService.getLivreursIndisponibles();
        colisNonAffectes = colisService.getColisNonAffectes();

        livreur = utilisateurService.getLivreurConnecte();
        if (livreur != null) {
            totalColis = colisService.countColisByLivreur(livreur.getId());
            colisEnTransit = colisService.countColisByLivreurEtStatus(livreur.getId(), StatusColis.EN_TRANSIT);
            colisTermines = colisService.countColisByLivreurEtStatus(livreur.getId(), StatusColis.LIVRE);
            troisDerniersColis = colisService.findDerniersColisByLivreur(livreur.getId(), 3);

            if (statusFiltre == null || statusFiltre.isEmpty()) {
                colisLivreur = colisService.getColisByLivreur(livreur);
            } else {
                colisLivreur = colisService.getColisByLivreurEtStatus(livreur, StatusColis.valueOf(statusFiltre));
            }

            nouveauxStatus.clear();
            for (Colis c : colisLivreur) {
                nouveauxStatus.put(c.getId(), c.getStatus().name());
            }
        }
    }

    public void loadColisDetail(ComponentSystemEvent event) {
        if (colisDetail == null && idColis != null) {
            colisDetail = colisService.findById(idColis);
        }
    }

    public void filtrerParStatus() {
        init();
    }

    public void affecterColisAuLivreur() {
        try {
            if (idLivreurSelectionne != null && idColisAffectation != null) {
                colisService.affecterColis(idColisAffectation, idLivreurSelectionne);
                rafraichirDonnees();
            } else {
                addErrorMessage("Identifiants manquants pour l'affectation");
            }
        } catch (Exception e) {
            addErrorMessage("Erreur lors de l'affectation : " + e.getMessage());
        }
    }

    public void preparerAffectation(Long livreurId) {
        livreurSelectionne = livreurService.getLivreurById(livreurId);
        if (livreurSelectionne == null || !"oui".equalsIgnoreCase(livreurSelectionne.getDisponibiliter())) {
            addErrorMessage("Livreur non disponible ou introuvable");
            return;
        }
        idLivreurSelectionne = livreurId;
        colisNonAffectes = colisService.getColisNonAffectes();
        idColisAffectation = null;
    }

    public void mettreAJourStatus(Long colisId) {
        String nouveauStatus = nouveauxStatus.get(colisId);
        if (nouveauStatus != null) {
            try {
                StatusColis statutEnum = StatusColis.valueOf(nouveauStatus.toUpperCase().replace(" ", "_"));
                colisService.mettreAJourStatusColis(colisId, statutEnum);
                init();
            } catch (IllegalArgumentException e) {
                addErrorMessage("Statut invalide : " + nouveauStatus);
            }
        }
    }

    public String afficherDetails(Long colisId) {
        return "colisDetails.xhtml?faces-redirect=true&colisId=" + colisId;
    }

    public String editerColis(Long idColis) {
        this.colisDetail = colisService.findById(idColis);
        return "editColis.xhtml?faces-redirect=true";
    }

    private void rafraichirDonnees() {
        livreursIndisponibles = livreurService.getLivreursIndisponibles();
        colisNonAffectes = colisService.getColisNonAffectes();
    }

    private void addMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    // Getters & Setters

    public List<Livreur> getLivreursIndisponibles() { return livreursIndisponibles; }
    public List<Colis> getColisNonAffectes() { return colisNonAffectes; }
    public int getNombreColisNonAffectes() { return colisNonAffectes != null ? colisNonAffectes.size() : 0; }

    public List<Colis> getColisLivreur() { return colisLivreur; }
    public List<Colis> getTroisDerniersColis() { return troisDerniersColis; }

    public int getTotalColis() { return totalColis; }
    public int getColisEnTransit() { return colisEnTransit; }
    public long getColisTermines() { return colisTermines; }

    public Long getIdLivreurSelectionne() { return idLivreurSelectionne; }
    public void setIdLivreurSelectionne(Long idLivreurSelectionne) { this.idLivreurSelectionne = idLivreurSelectionne; }

    public Long getIdColisAffectation() { return idColisAffectation; }
    public void setIdColisAffectation(Long idColisAffectation) { this.idColisAffectation = idColisAffectation; }

    public Long getIdColis() { return idColis; }
    public void setIdColis(Long idColis) { this.idColis = idColis; }

    public Colis getColisDetail() { return colisDetail; }
    public void setColisDetail(Colis colisDetail) { this.colisDetail = colisDetail; }

    public String getStatusFiltre() { return statusFiltre; }
    public void setStatusFiltre(String statusFiltre) { this.statusFiltre = statusFiltre; }

    public Map<Long, String> getNouveauxStatus() { return nouveauxStatus; }
    public void setNouveauxStatus(Map<Long, String> nouveauxStatus) { this.nouveauxStatus = nouveauxStatus; }

    public Map<String, String> getListeStatus() {
        Map<String, String> liste = new LinkedHashMap<>();
        for (StatusColis s : StatusColis.values()) {
            liste.put(s.name(), s.toString().replace("_", " "));
        }
        return liste;
    }
    public String formatDateLivraison(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    public String formatDateEnvoi(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }
    public Livreur getLivreurConnecte() { return livreur; }
    public Livreur getLivreurSelectionne() { return livreurSelectionne; }
    public void setLivreurSelectionne(Livreur livreurSelectionne) { this.livreurSelectionne = livreurSelectionne; }
}
