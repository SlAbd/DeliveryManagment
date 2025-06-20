package com.project.deliveryms.services;

import com.project.deliveryms.entities.BordereauExpedition;
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

    @Inject
    private AdresseService adresseService;

    // Création d'un colis sans utilisateur
    public Colis createColis(String description, double poids, Adresse adresseDestinataire) {
        Colis colis = new Colis();
        colis.setNumeroSuivi(UUID.randomUUID().toString());
        colis.setDescription(description);
        colis.setPoids(poids);
        colis.setDateEnvoi(LocalDateTime.now());
        colis.setStatus(StatusColis.EN_ATTENTE);
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
        Colis colis = colisRepository.findById(colisId);

        if (colis == null) {
            throw new EntityNotFoundException("Colis avec l'ID " + colisId + " non trouvé");
        }

        colis.setDeleted(true);
        colisRepository.save(colis);
    }

    public Colis updateColis(Long colisId, String description, double poids, StatusColis status,
                             String rue, String ville, String codePostal, String pays) {

        Colis colis = colisRepository.findById(colisId);

        if (colis == null) {
            throw new EntityNotFoundException("Colis avec l'ID " + colisId + " non trouvé");
        }

        colis.setDescription(description);
        colis.setPoids(poids);
        colis.setStatus(status);

        Adresse adresse = colis.getAdresseDestinataire();
        if (adresse != null) {
            adresse.setRue(rue);
            adresse.setVille(ville);
            adresse.setCodePostal(codePostal);
            adresse.setPays(pays);

            adresseService.updateAdresse(adresse);
        } else {
            adresse = adresseService.createAdresse(rue, ville, codePostal, pays);
            colis.setAdresseDestinataire(adresse);
        }

        colisRepository.update(colis);
        return colis;
    }

    public BordereauExpedition getBordereauByColisId(Long colisId) {
        Colis colis = em.find(Colis.class, colisId);
        if (colis != null) {
            return colis.getBordereauExpedition();
        }
        return null;
    }

    // **Méthode ajoutée** : Récupérer colis par ID
    public Colis getColisById(Long colisId) {
        return colisRepository.findById(colisId);
    }

    // **Méthode ajoutée** : Sauvegarder colis (update ou persist)
    public void saveColis(Colis colis) {
        colisRepository.update(colis);
    }
}
