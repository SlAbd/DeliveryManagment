package com.project.deliveryms.repositories;

import com.project.deliveryms.entities.Colis;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

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

}
