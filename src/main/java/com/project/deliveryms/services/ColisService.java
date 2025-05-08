package com.project.deliveryms.services;

import com.project.deliveryms.entities.Adresse;
import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.repositories.ColisRepository;
import com.project.deliveryms.repositories.LivreureRepository;
import com.project.deliveryms.repositories.UtilisateurRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
@Transactional
public class ColisService {
    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;
    @Inject
    private AdresseService adresseService;
    @PersistenceContext
    private EntityManager em;

    @Inject
    private LivreureRepository livreurRepository;
    @Inject
    private UtilisateurRepository utilisateurRepository;
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

    // Création d'un colis sans utilisateur
    public Colis createColis(String description, double poids, Adresse adresseDestinataire) {
        Colis colis = new Colis();
        colis.setNumeroSuivi(UUID.randomUUID().toString()); // Génération du numéro de suivi unique
        colis.setDescription(description);
        colis.setPoids(poids);
        colis.setDateEnvoi(LocalDateTime.now());
        colis.setStatus(StatusColis.EN_ATTENTE); // Le colis est en attente initialement
        colis.setAdresseDestinataire(adresseDestinataire);

        em.persist(colis);
        return colis;
    }

    public Colis associerColisAUtilisateur(Long colisId, Long utilisateurId) {
        Colis colis = colisRepository.findById(colisId);
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId);

        if (colis == null || utilisateur == null) {
            throw new IllegalArgumentException("Colis ou utilisateur non trouvé");
        }

        colis.setUtilisateur(utilisateur);
        colisRepository.update(colis);
        return colis;
    }



    public List<Colis> getAllColisWithDetails() {
        return colisRepository.findAllWithDetails();
    }

    public void deleteColis(Long colisId) {
        // Recherche le colis par ID
        Colis colis = colisRepository.findById(colisId);

        if (colis == null) {
            // Si colis non trouvé, lancer une exception
            throw new EntityNotFoundException("Colis avec l'ID " + colisId + " non trouvé");
        }

        // Marquer le colis comme supprimé
        colis.setDeleted(true);

        // Sauvegarder le colis avec l'attribut 'deleted' mis à jour
        colisRepository.save(colis);
    }
}