package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.services.ColisService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class MesColisBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Colis> userColis = new ArrayList<>();
    private List<Colis> filteredColis = new ArrayList<>();
    
    // Filtres
    private String filtreTypeStatus = "";
    private LocalDateTime filtreDateDebut;
    private LocalDateTime filtreDateFin;
    
    // Pagination
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalItems;

    @Inject
    private ColisService colisService;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        loadUserColis();
        filteredColis = new ArrayList<>(userColis);
        calculateTotalItems();
    }

    /**
     * Charge tous les colis de l'utilisateur connecté
     */
    private void loadUserColis() {
        Utilisateur utilisateur = loginBean.getUtilisateur();
        if (utilisateur != null) {
            // Récupérer tous les colis et filtrer par utilisateur
            List<Colis> allColis = colisService.getAllColisWithDetails();
            userColis = allColis.stream()
                    .filter(c -> c.getUtilisateur() != null && 
                           c.getUtilisateur().getId().equals(utilisateur.getId()) &&
                           !c.getDeleted())
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Applique les filtres sur la liste des colis
     */
    public String filtrer() {
        filteredColis = userColis;
        
        // Filtre par statut
        if (filtreTypeStatus != null && !filtreTypeStatus.isEmpty()) {
            filteredColis = filteredColis.stream()
                .filter(c -> c.getStatus() != null && c.getStatus().toString().equals(filtreTypeStatus))
                .collect(Collectors.toList());
        }
        
        // Filtre par date de début
        if (filtreDateDebut != null) {
            filteredColis = filteredColis.stream()
                .filter(c -> c.getDateEnvoi() != null && c.getDateEnvoi().isAfter(filtreDateDebut))
                .collect(Collectors.toList());
        }
        
        // Filtre par date de fin
        if (filtreDateFin != null) {
            filteredColis = filteredColis.stream()
                .filter(c -> c.getDateEnvoi() != null && c.getDateEnvoi().isBefore(filtreDateFin))
                .collect(Collectors.toList());
        }
        
        calculateTotalItems();
        currentPage = 1; // Revenir à la première page après filtrage
        
        return null;
    }
    
    /**
     * Réinitialise les filtres
     */
    public String reinitialiserFiltres() {
        filtreTypeStatus = "";
        filtreDateDebut = null;
        filtreDateFin = null;
        filteredColis = new ArrayList<>(userColis);
        calculateTotalItems();
        currentPage = 1;
        
        return null;
    }
    
    /**
     * Calcule le nombre total d'éléments après filtrage
     */
    private void calculateTotalItems() {
        this.totalItems = filteredColis != null ? filteredColis.size() : 0;
    }
    
    /**
     * Retourne les colis de la page courante
     */
    public List<Colis> getCurrentPageItems() {
        if (filteredColis == null || filteredColis.isEmpty()) {
            return new ArrayList<>();
        }

        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= filteredColis.size()) {
            currentPage = 1;
            fromIndex = 0;
        }

        int toIndex = Math.min(fromIndex + pageSize, filteredColis.size());

        return filteredColis.subList(fromIndex, toIndex);
    }
    
    /**
     * Formatte une date au format dd/MM/yyyy
     */
    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }
    
    /**
     * Retourne la classe CSS pour le statut d'un colis
     */
    public String getStatusClass(StatusColis status) {
        if (status == null) {
            return "";
        }
        
        switch (status) {
            case EN_TRANSIT:
                return "bg-yellow-100 text-yellow-800";
            case LIVRE:
                return "bg-green-100 text-green-800";
            case EN_ATTENTE:
                return "bg-blue-100 text-blue-800";
            case ANNULE:
                return "bg-red-100 text-red-800";
            case RETOURNE:
                return "bg-gray-100 text-gray-800";
            default:
                return "";
        }
    }
    
    /**
     * Retourne le nombre total de pages
     */
    public int getTotalPages() {
        if (totalItems == 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalItems / pageSize);
    }
    
    /**
     * Retourne l'index du premier élément de la page courante
     */
    public int getFirstItemIndex() {
        if (totalItems == 0) {
            return 0;
        }
        return (currentPage - 1) * pageSize + 1;
    }
    
    /**
     * Retourne l'index du dernier élément de la page courante
     */
    public int getLastItemIndex() {
        if (totalItems == 0) {
            return 0;
        }
        return Math.min(getFirstItemIndex() + pageSize - 1, totalItems);
    }
    
    // Getters et Setters
    
    public List<Colis> getUserColis() {
        return userColis;
    }
    
    public String getFiltreTypeStatus() {
        return filtreTypeStatus;
    }
    
    public void setFiltreTypeStatus(String filtreTypeStatus) {
        this.filtreTypeStatus = filtreTypeStatus;
    }
    
    public LocalDateTime getFiltreDateDebut() {
        return filtreDateDebut;
    }
    
    public void setFiltreDateDebut(LocalDateTime filtreDateDebut) {
        this.filtreDateDebut = filtreDateDebut;
    }
    
    public LocalDateTime getFiltreDateFin() {
        return filtreDateFin;
    }
    
    public void setFiltreDateFin(LocalDateTime filtreDateFin) {
        this.filtreDateFin = filtreDateFin;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
}