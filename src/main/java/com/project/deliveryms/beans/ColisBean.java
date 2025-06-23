package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Adresse;
import com.project.deliveryms.services.ColisService;
import com.project.deliveryms.services.AdresseService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityNotFoundException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

@Named
@RequestScoped
public class ColisBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Colis nouveauColis;

    // Temporary form fields (could also bind directly to nouveauColis properties)
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private String description;
    private double poids;
    private List<Colis> listeColis;

    // Variables pour la pagination
    private int currentPage = 1;
    private int pageSize = 10; // Renommé de itemsPerPage à pageSize pour cohérence avec l'UI
    private int totalItems;

    @Inject
    private ColisService colisService;

    @Inject
    private AdresseService adresseService;

    @PostConstruct
    public void init() {
        nouveauColis = new Colis();
        chargerListeColis();
        calculateTotalItems();
        resetFields();
    }

    public void resetFields() {
        description = "";
        poids = 0.0;
        rue = "";
        ville = "";
        codePostal = "";
        pays = "";
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(formatter) : "";
    }

    // Méthode pour créer un colis
    public String ajouterColis() {
        // Créer une adresse à partir des informations du formulaire
        Adresse adresse = adresseService.createAdresse(rue, ville, codePostal, pays);

        // Utiliser le service pour créer le colis
        Colis colis = colisService.createColis(description, poids, adresse);

        // Recharger la liste après ajout
        chargerListeColis();
        calculateTotalItems();

        // Rediriger ou effectuer d'autres actions après la création du colis
        return "success?faces-redirect=true"; // Exemple de redirection après la création
    }

    // Méthode pour associer un colis à un utilisateur
    public String associerColisAUtilisateur(Long colisId, Long utilisateurId) {
        try {
            // Associer le colis à un utilisateur
            colisService.associerColisAUtilisateur(colisId, utilisateurId);
            return "success?faces-redirect=true"; // Redirection après succès
        } catch (IllegalArgumentException e) {
            // Gérer l'erreur si le colis ou l'utilisateur n'est pas trouvé
            return "error?faces-redirect=true"; // Exemple de redirection en cas d'erreur
        }
    }

    public void chargerListeColis() {
        this.listeColis = colisService.getAllColisWithDetails();
    }

    private void calculateTotalItems() {
        this.totalItems = listeColis != null ? listeColis.size() : 0;
    }

    private Long colisId;

    public void supprimerColis() {
        try {
            colisService.deleteColis(colisId); // Appel du service pour marquer le colis comme supprimé
            // Afficher un message ou effectuer une redirection après la suppression
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Colis supprimé avec succès"));
        } catch (EntityNotFoundException e) {
            // Gérer l'exception si le colis n'a pas été trouvé
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Le colis n'a pas été trouvé"));
        }
    }


    // Méthodes de pagination améliorées

    // Aller à la première page
    public void firstPage() {
        currentPage = 1;
    }

    // Aller à la dernière page
    public void lastPage() {
        currentPage = getTotalPages();
    }

    // Aller à la page suivante
    public void nextPage() {
        if (currentPage < getTotalPages()) {
            currentPage++;
        }
    }

    // Aller à la page précédente
    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
        }
    }

    // Aller à une page spécifique
    public void goToPage(int page) {
        if (page >= 1 && page <= getTotalPages()) {
            currentPage = page;
        }
    }

    // Calculer le nombre total de pages
    public int getTotalPages() {
        if (totalItems == 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    // Obtenir les numéros de page à afficher
    public List<Integer> getPageNumbers() {
        List<Integer> pages = new ArrayList<>();
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(getTotalPages(), currentPage + 2);

        for (int i = startPage; i <= endPage; i++) {
            pages.add(i);
        }

        return pages;
    }

    // Méthode pour maintenir la compatibilité avec le code existant
    public List<Colis> getCurrentPageItems() {
        return getListeColis(); // Utilise la méthode de pagination que nous avons implémentée
    }

    // Si vous préférez garder itemsPerPage comme nom de variable
    public int getItemsPerPage() {
        return pageSize;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.pageSize = itemsPerPage;
        // Réinitialiser à la première page quand on change le nombre d'éléments par page
        this.currentPage = 1;
    }

    // Obtenir les éléments de la page courante
    public List<Colis> getListeColis() {
        if (listeColis == null || listeColis.isEmpty()) {
            return new ArrayList<>();
        }

        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= listeColis.size()) {
            // Si l'index de début est hors limites, revenir à la première page
            currentPage = 1;
            fromIndex = 0;
        }

        int toIndex = Math.min(fromIndex + pageSize, listeColis.size());

        return listeColis.subList(fromIndex, toIndex);
    }

    // Obtenir l'index du premier élément affiché
    public int getFirstItemIndex() {
        if (totalItems == 0) {
            return 0;
        }
        return (currentPage - 1) * pageSize;
    }

    // Obtenir l'index du dernier élément affiché
    public int getLastItemIndex() {
        if (totalItems == 0) {
            return 0;
        }
        return Math.min(getFirstItemIndex() + pageSize, totalItems);
    }

    // Obtenir le nombre total d'éléments
    public int getTotalItems() {
        return totalItems;
    }

    // Getters et setters
    public void setListeColis(List<Colis> listeColis) {
        this.listeColis = listeColis;
        calculateTotalItems();
    }

    public Colis getNouveauColis() {
        return nouveauColis;
    }

    public void setNouveauColis(Colis nouveauColis) {
        this.nouveauColis = nouveauColis;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
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
        this.poids = poids;
    }

    // Getters et setters pour la pagination
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        // Réinitialiser à la première page quand on change le nombre d'éléments par page
        this.currentPage = 1;
    }

    public Long getColisId() {
        return colisId;
    }

    public void setColisId(Long colisId) {
        this.colisId = colisId;
    }
}