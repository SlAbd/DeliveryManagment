package com.project.deliveryms.repositories;

import com.project.deliveryms.entities.Colis;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class ColisRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Colis colis) {
        em.persist(colis);
    }

    public Colis findById(Long colisId) {
        try {
            return em.createQuery("SELECT c FROM Colis c WHERE c.id = :id", Colis.class)
                    .setParameter("id", colisId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Colis findByNumeroSuivi(String numero) {
        return em.createQuery(
                        "SELECT c FROM Colis c WHERE c.numeroSuivi = :numero", Colis.class)
                .setParameter("numero", numero)
                .getSingleResult();
    }

    public List<Colis> findAllWithDetails() {
        return em.createQuery(
                "SELECT c FROM Colis c LEFT JOIN FETCH c.adresseDestinataire LEFT JOIN FETCH c.utilisateur WHERE c.deleted = false",
                Colis.class
        ).getResultList();
    }



    public void update(Colis colis) {
        em.merge(colis);
    }
}
