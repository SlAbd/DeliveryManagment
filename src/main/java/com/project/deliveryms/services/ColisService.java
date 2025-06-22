package com.project.deliveryms.services;

import com.project.deliveryms.entities.*;
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
import java.util.Optional;
import java.util.UUID;

@Stateless
@Transactional
public class ColisService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Inject
    private AdresseService adresseService;
    @Inject
    private ColisRepository colisRepository;
    @Inject
    private LivreureRepository livreurRepository;
    @Inject
    private UtilisateurRepository utilisateurRepository;

    public Colis createColis(Colis colis) {
        em.persist(colis);
        return colis;
    }

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

    public Colis updateStatut(Long colisId, StatusColis statut) {
        Colis colis = em.find(Colis.class, colisId);
        if (colis != null) {
            colis.setStatus(statut);
            em.merge(colis);
        }
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
        if (colis == null) throw new EntityNotFoundException("Colis avec l'ID " + colisId + " non trouvé");
        colis.setDeleted(true);
        colisRepository.save(colis);
    }

    public Colis updateColis(Long colisId, String description, double poids, StatusColis status,
                             String rue, String ville, String codePostal, String pays) {
        Colis colis = colisRepository.findById(colisId);
        if (colis == null) throw new EntityNotFoundException("Colis avec l'ID " + colisId + " non trouvé");

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

    public Colis associerColisAUtilisateur(Long colisId, Long utilisateurId) {
        Colis colis = colisRepository.findById(colisId);
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId);
        if (colis == null || utilisateur == null)
            throw new IllegalArgumentException("Colis ou utilisateur non trouvé");

        colis.setUtilisateur(utilisateur);
        colisRepository.update(colis);
        return colis;
    }

    public void affecterColisALivreur(Long idColis, Long idLivreur) {
        Colis colis = em.find(Colis.class, idColis);
        Livreur livreur = em.find(Livreur.class, idLivreur);
        if (colis != null && livreur != null) {
            colis.setLivreur(livreur);
            em.merge(colis);
        }
    }

    public void affecterColis(Long idColis, Long idLivreur) {
        Colis colis = colisRepository.find(idColis);
        Livreur livreur = livreurRepository.find(idLivreur);
        if (colis == null || livreur == null) throw new RuntimeException("Colis ou livreur introuvable");

        colis.setLivreur(livreur);
        colis.setStatus(StatusColis.EN_TRANSIT);
        livreur.setDisponibiliter("non");
        colisRepository.save(colis);
    }

    public List<Colis> getColisNonAffectes() {
        return colisRepository.findColisNonAffectes();
    }

    public List<Colis> getColisByLivreur(Livreur livreur) {
        return em.createQuery("SELECT c FROM Colis c WHERE c.livreur = :livreur", Colis.class)
                .setParameter("livreur", livreur)
                .getResultList();
    }

    public void update(Colis colis) {
        em.merge(colis);
    }

    public void updateStatusColis(Long colisId, StatusColis nouveauStatus) {
        Colis colis = colisRepository.findById(colisId);
        if (colis == null) throw new EntityNotFoundException("Colis non trouvé");
        colis.setStatus(nouveauStatus);
        colisRepository.update(colis);
    }

    public void mettreAJourStatusColis(Long colisId, StatusColis nouveauStatut) {
        Optional<Colis> colisOptional = colisRepository.findByIdcolis(colisId);
        colisOptional.ifPresent(colis -> {
            colis.setStatus(nouveauStatut);
            colisRepository.save(colis);
        });
    }

    public List<Livreur> getLivreursIndisponibles() {
        return livreurRepository.findLivreursIndisponibles();
    }

    public List<Colis> findAll() {
        return em.createQuery("SELECT c FROM Colis c", Colis.class).getResultList();
    }

    public List<Colis> findAllNonDeleted() {
        return em.createQuery("SELECT c FROM Colis c WHERE c.deleted = false", Colis.class).getResultList();
    }

    public List<Colis> findByStatus(StatusColis status) {
        return em.createQuery("SELECT c FROM Colis c WHERE c.status = :status AND c.deleted = false", Colis.class)
                .setParameter("status", status).getResultList();
    }

    public List<Colis> findByUser(Utilisateur utilisateur) {
        return em.createQuery("SELECT c FROM Colis c WHERE c.utilisateur = :utilisateur AND c.deleted = false", Colis.class)
                .setParameter("utilisateur", utilisateur).getResultList();
    }

    public List<Colis> findByUserId(Long userId) {
        return em.createQuery("SELECT c FROM Colis c WHERE c.utilisateur.id = :userId AND c.deleted = false", Colis.class)
                .setParameter("userId", userId).getResultList();
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
        return em.createQuery("SELECT COUNT(c) FROM Colis c WHERE c.utilisateur.id = :userId AND c.deleted = false", Long.class)
                .setParameter("userId", userId).getSingleResult().intValue();
    }

    public int countColisByLivreur(Long livreurId) {
        return em.createQuery("SELECT COUNT(c) FROM Colis c WHERE c.livreur.id = :livreurId", Long.class)
                .setParameter("livreurId", livreurId).getSingleResult().intValue();
    }

    public int countColisByLivreurEtStatus(Long livreurId, StatusColis status) {
        return em.createQuery("SELECT COUNT(c) FROM Colis c WHERE c.livreur.id = :livreurId AND c.status = :status", Long.class)
                .setParameter("livreurId", livreurId)
                .setParameter("status", status)
                .getSingleResult().intValue();
    }

    public List<Colis> findDerniersColisByLivreur(Long livreurId, int limit) {
        return em.createQuery("SELECT c FROM Colis c WHERE c.livreur.id = :livreurId ORDER BY c.dateEnvoi DESC", Colis.class)
                .setParameter("livreurId", livreurId)
                .setMaxResults(limit)
                .getResultList();
    }
    public List<Colis> getColisByLivreurEtStatus(Livreur livreur, StatusColis status) {
        return em.createQuery("SELECT c FROM Colis c WHERE c.livreur = :livreur AND c.status = :status", Colis.class)
                .setParameter("livreur", livreur)
                .setParameter("status", status)
                .getResultList();
    }
    public BordereauExpedition getBordereauByColisId(Long colisId) {
        Colis colis = em.find(Colis.class, colisId);
        if (colis != null) {
            return colis.getBordereauExpedition();
        }
        return null;
    }
}