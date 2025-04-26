package com.project.deliveryms.repositories;

import com.project.deliveryms.entities.Adresse;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
@Stateless
public class AdresseRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Adresse save(Adresse adresse) {
        if (adresse.getId() == null) {
            entityManager.persist(adresse);
            return adresse;
        } else {
            return entityManager.merge(adresse);
        }
    }
}
