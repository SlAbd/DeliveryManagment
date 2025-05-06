package com.project.deliveryms.services;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.repositories.ColisRepository;
import com.project.deliveryms.repositories.LivreureRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
@Transactional
public class ColisService {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Inject
    private AdresseService adresseService;


    @Inject
    private LivreureRepository livreurRepository;

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


    @Transactional
    public void affecterColisALivreur(Long idColis, Long idLivreur) {
        Colis colis = entityManager.find(Colis.class, idColis);
        Livreur livreur = entityManager.find(Livreur.class, idLivreur);

        if (colis != null && livreur != null) {
            colis.setLivreur(livreur);
            entityManager.merge(colis);
        }
    }


    @Inject
    private ColisRepository colisRepository;

    public List<Livreur> getLivreursIndisponibles() {
        return livreurRepository.findLivreursIndisponibles();
    }


    public void affecterColis(Long idColis, Long idLivreur) {
        Colis colis = colisRepository.find(idColis);
        if (colis == null) {
            throw new RuntimeException("Colis introuvable");
        }

        Livreur livreur = livreurRepository.find(idLivreur);
        if (livreur == null) {
            throw new RuntimeException("Livreur introuvable");
        }

        colis.setLivreur(livreur);
        colis.setStatus(StatusColis.EN_TRANSIT); // Utilise l'enum correctement
        livreur.setDisponibiliter("non");
        colisRepository.save(colis);
    }
    public List<Colis> getColisNonAffectes() {
        try {
            return colisRepository.findColisNonAffectes();
        } catch (Exception e) {
            // Log or rethrow the exception as needed
            throw new RuntimeException("Erreur lors de la récupération des colis non affectés.", e);
        }
    }

}