package com.project.deliveryms.services;

import com.project.deliveryms.entities.Adresse;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
@Transactional
public class AdresseService {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    public Adresse createAdresse(String rue, String ville, String codePostal, String pays) {
        Adresse adresse = new Adresse();
        adresse.setRue(rue);
        adresse.setVille(ville);
        adresse.setCodePostal(codePostal);
        adresse.setPays(pays);

        entityManager.persist(adresse);
        return adresse;
    }
}