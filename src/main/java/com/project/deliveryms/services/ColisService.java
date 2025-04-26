package com.project.deliveryms.services;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.enums.StatusColis;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
@Transactional
public class ColisService {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Inject
    private AdresseService adresseService;

    public Colis createColis(Colis colis) {
        entityManager.persist(colis);
        return colis;
    }

    public Colis updateStatut(Long colisId, StatusColis statut) {
        Colis colis = entityManager.find(Colis.class, colisId);
        if (colis != null) {
            colis.setStatus(statut);
            entityManager.merge(colis);
        }
        return colis;
    }

    public Colis getColisByNumeroSuivi(String numeroSuivi) {
        return entityManager.createQuery(
                        "SELECT c FROM Colis c WHERE c.numeroSuivi = :numeroSuivi", Colis.class)
                .setParameter("numeroSuivi", numeroSuivi)
                .getSingleResult();
    }
}