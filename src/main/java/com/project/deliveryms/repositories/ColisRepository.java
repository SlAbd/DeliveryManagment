package com.project.deliveryms.repositories;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.enums.StatusColis;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class ColisRepository  {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Colis colis) {
        entityManager.persist(colis);
    }

    public Colis find(Long id) {
        return entityManager.find(Colis.class, id);
    }

    public Colis findByNumeroSuivi(String numeroSuivi) {
        TypedQuery<Colis> query = entityManager.createQuery(
                "SELECT c FROM Colis c WHERE c.numeroSuivi = :numeroSuivi", Colis.class);
        query.setParameter("numeroSuivi", numeroSuivi);
        return query.getResultList().stream().findFirst().orElse(null);
    }




    public List<Colis> findColisNonAffectes() {
        TypedQuery<Colis> query = entityManager.createQuery(
                "SELECT c FROM Colis c WHERE (c.livreur IS NULL OR c.status IS NULL OR c.status = :status)",
                Colis.class
        );
        query.setParameter("status", StatusColis.EN_ATTENTE); // Pour récupérer les colis avec statut "EN_ATTENTE"

        return query.getResultList();
    }


}
