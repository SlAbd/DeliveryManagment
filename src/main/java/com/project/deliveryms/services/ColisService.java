package com.project.deliveryms.services;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.entities.Adresse;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.repositories.ColisRepository;
import com.project.deliveryms.repositories.UtilisateurRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class ColisService {


    @PersistenceContext
    private EntityManager em;

    @Inject
    private ColisRepository colisRepository;

    @Inject
    private UtilisateurRepository utilisateurRepository;

    @Inject
    private AdresseService adresseService;


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

    public Colis getColisByNumeroSuivi(String numeroSuivi) {
        return colisRepository.findByNumeroSuivi(numeroSuivi);
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

    /**
     * Méthode pour mettre à jour les informations d'un colis
     * @param colisId ID du colis à mettre à jour
     * @param description Nouvelle description
     * @param poids Nouveau poids
     * @param status Nouveau statut
     * @param rue Nouvelle rue de l'adresse
     * @param ville Nouvelle ville
     * @param codePostal Nouveau code postal
     * @param pays Nouveau pays
     * @return Le colis mis à jour
     * @throws EntityNotFoundException si le colis n'est pas trouvé
     */
    public Colis updateColis(Long colisId, String description, double poids, StatusColis status,
                             String rue, String ville, String codePostal, String pays) {

        // Recherche le colis par ID
        Colis colis = colisRepository.findById(colisId);

        if (colis == null) {
            throw new EntityNotFoundException("Colis avec l'ID " + colisId + " non trouvé");
        }

        // Mise à jour des informations du colis
        colis.setDescription(description);
        colis.setPoids(poids);
        colis.setStatus(status);

        // Mise à jour de l'adresse
        Adresse adresse = colis.getAdresseDestinataire();
        if (adresse != null) {
            // Mise à jour de l'adresse existante
            adresse.setRue(rue);
            adresse.setVille(ville);
            adresse.setCodePostal(codePostal);
            adresse.setPays(pays);

            // Enregistrer les modifications de l'adresse
            adresseService.updateAdresse(adresse);
        } else {
            // Création d'une nouvelle adresse si elle n'existe pas
            adresse = adresseService.createAdresse(rue, ville, codePostal, pays);
            colis.setAdresseDestinataire(adresse);
        }

        // Sauvegarde du colis mis à jour
         colisRepository.update(colis);

        return colis;
    }
    /// //////////////////////////
    public List<Colis> findAll() {
        TypedQuery<Colis> query = em.createQuery("SELECT c FROM Colis c", Colis.class);
        return query.getResultList();
    }

    public List<Colis> findAllNonDeleted() {
        TypedQuery<Colis> query = em.createQuery("SELECT c FROM Colis c WHERE c.deleted = false", Colis.class);
        return query.getResultList();
    }

    public List<Colis> findByStatus(StatusColis status) {
        TypedQuery<Colis> query = em.createQuery("SELECT c FROM Colis c WHERE c.status = :status AND c.deleted = false", Colis.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Colis> findByUser(Utilisateur utilisateur) {
        TypedQuery<Colis> query = em.createQuery("SELECT c FROM Colis c WHERE c.utilisateur = :utilisateur AND c.deleted = false", Colis.class);
        query.setParameter("utilisateur", utilisateur);
        return query.getResultList();
    }

    public List<Colis> findByUserId(Long userId) {
        TypedQuery<Colis> query = em.createQuery("SELECT c FROM Colis c WHERE c.utilisateur.id = :userId AND c.deleted = false", Colis.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public Colis findById(Long id) {
        return em.find(Colis.class, id);
    }

    public Colis save(Colis colis) {
        if (colis.getId() == null) {
            em.persist(colis);
            return colis;
        } else {
            return em.merge(colis);
        }
    }

    public void remove(Long id) {
        Colis colis = findById(id);
        if (colis != null) {
            colis.setDeleted(true);
            em.merge(colis);
        }
    }

    public int countColisByUserId(Long userId) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Colis c WHERE c.utilisateur.id = :userId AND c.deleted = false", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue();
    }
}