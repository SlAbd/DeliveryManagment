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




}
