package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.Role;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("adminBean")
@RequestScoped
public class AdminDashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private LoginBean loginBean;

    private List<Colis> recentDeliveries;
    private int totalColis;
    private int colisEnAttente;
    private int colisEnTransit;
    private int livreursActifs;
    private String totalColisPercentage;
    private String colisEnAttentePercentage;
    private String colisEnTransitPercentage;
    private String livreursActifsChange;

    @PostConstruct
    public void init() {
        loadStatistics();
        loadRecentDeliveries();
    }

    private void loadStatistics() {
        // Total colis
        TypedQuery<Long> totalQuery = entityManager.createQuery(
                "SELECT COUNT(c) FROM Colis c WHERE c.deleted = false",
                Long.class
        );
        totalColis = totalQuery.getSingleResult().intValue();

        // Colis en attente
        TypedQuery<Long> enAttenteQuery = entityManager.createQuery(
                "SELECT COUNT(c) FROM Colis c WHERE c.status = 'EN_ATTENTE' AND c.deleted = false",
                Long.class
        );
        colisEnAttente = enAttenteQuery.getSingleResult().intValue();

        // Colis en transit
        TypedQuery<Long> enTransitQuery = entityManager.createQuery(
                "SELECT COUNT(c) FROM Colis c WHERE c.status = 'EN_TRANSIT' AND c.deleted = false",
                Long.class
        );
        colisEnTransit = enTransitQuery.getSingleResult().intValue();

        // Livreurs actifs
        TypedQuery<Long> livreursQuery = entityManager.createQuery(
                "SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role",
                Long.class
        );
        livreursQuery.setParameter("role", Role.LIVREUR);
        livreursActifs = livreursQuery.getSingleResult().intValue();

        // Calculate percentages (for demonstration, using fixed values)
        totalColisPercentage = "+12%";
        colisEnAttentePercentage = "+5%";
        colisEnTransitPercentage = "+18%";
        livreursActifsChange = "+3";
    }

    private void loadRecentDeliveries() {
        TypedQuery<Colis> query = entityManager.createQuery(
                "SELECT c FROM Colis c WHERE c.deleted = false ORDER BY c.dateEnvoi DESC",
                Colis.class
        );
        query.setMaxResults(10);
        recentDeliveries = query.getResultList();
    }

    public String viewDeliveryDetails(Long id) {
        // Implementation for viewing delivery details
        return "/admin/colis-details.xhtml?faces-redirect=true&id=" + id;
    }

    // Getters
    public List<Colis> getRecentDeliveries() {
        return recentDeliveries;
    }

    public int getTotalColis() {
        return totalColis;
    }

    public int getColisEnAttente() {
        return colisEnAttente;
    }

    public int getColisEnTransit() {
        return colisEnTransit;
    }

    public int getLivreursActifs() {
        return livreursActifs;
    }

    public String getTotalColisPercentage() {
        return totalColisPercentage;
    }

    public String getColisEnAttentePercentage() {
        return colisEnAttentePercentage;
    }

    public String getColisEnTransitPercentage() {
        return colisEnTransitPercentage;
    }

    public String getLivreursActifsChange() {
        return livreursActifsChange;
    }

    public Utilisateur getCurrentUser() {
        return loginBean.getUtilisateur();
    }
}