package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.Role;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class AdminClientsBean implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    private List<Utilisateur> clientsWithPackages;

    @PostConstruct
    public void init() {
        loadClientsWithPackages();
    }

    /**
     * Charge tous les clients ayant au moins un colis
     */
    private void loadClientsWithPackages() {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM Utilisateur u JOIN u.colis c WHERE u.role = :role AND c.deleted = false",
                Utilisateur.class
        );
        query.setParameter("role", Role.CLIENT);
        this.clientsWithPackages = query.getResultList();
    }

    /**
     * Retourne la liste des clients ayant au moins un colis
     */
    public List<Utilisateur> getClientsWithPackages() {
        return clientsWithPackages;
    }

    /**
     * Compte le nombre de colis pour un client donné
     */
    public long countPackagesForClient(Long clientId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Colis c WHERE c.utilisateur.id = :clientId AND c.deleted = false",
                Long.class
        );
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }

    /**
     * Récupère la date du dernier colis envoyé par un client
     */
    public String getLastActivityDate(Long clientId) {
        TypedQuery<java.time.LocalDateTime> query = entityManager.createQuery(
                "SELECT MAX(c.dateEnvoi) FROM Colis c WHERE c.utilisateur.id = :clientId AND c.deleted = false",
                java.time.LocalDateTime.class
        );
        query.setParameter("clientId", clientId);
        java.time.LocalDateTime date = query.getSingleResult();
        return date != null ? date.toLocalDate().toString() : "N/A";
    }
}
