package com.project.deliveryms.services;

import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.Role;
import com.project.deliveryms.repositories.UtilisateurRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class UtilisateurService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UtilisateurRepository utilisateurRepository;

    // Authentifier un utilisateur
    public String authentifier(String email, String motDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);

        if (utilisateur == null) {
            return "Utilisateur non trouvé";
        }

        if (!BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse())) {
            return "Mot de passe incorrect";
        }

        return "Connexion réussie";
    }

    // Méthode pour inscrire un nouvel utilisateur
    @Transactional
    public String inscrire(Utilisateur utilisateur) {
        // Vérifier si un utilisateur avec cet email existe déjà
        Utilisateur utilisateurExistant = utilisateurRepository.findByEmail(utilisateur.getEmail());

        if (utilisateurExistant != null) {
            return "Email déjà utilisé. Veuillez en choisir un autre.";
        }

        utilisateur.setCreationDate(java.time.LocalDateTime.now());
        utilisateur.setLastConnectionDate(java.time.LocalDateTime.now());

        // Hashing du mot de passe avant de l'enregistrer
        String motDePasseHash = BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
        utilisateur.setMotDePasse(motDePasseHash);

        // Enregistrer l'utilisateur dans la base de données
        utilisateurRepository.save(utilisateur);
        return "Inscription réussie";
    }

    public Utilisateur findUserByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Méthode pour mettre à jour un utilisateur
    @Transactional
    public void update(Utilisateur utilisateur) {
        // Recherche de l'utilisateur par son identifiant
        Utilisateur existingUser = entityManager.find(Utilisateur.class, utilisateur.getId());
        if (existingUser != null) {
            // Mise à jour des informations de l'utilisateur
            existingUser.setNom(utilisateur.getNom());
            existingUser.setPrenom(utilisateur.getPrenom());
            existingUser.setEmail(utilisateur.getEmail());

            // Si le mot de passe a changé, hasher et mettre à jour le mot de passe
            if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
                String motDePasseHash = BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
                existingUser.setMotDePasse(motDePasseHash);
            }
        }
    }
    /**public List<Utilisateur> findClientsWithPackages() {
        String jpql = "SELECT u FROM Utilisateur u WHERE SIZE(u.colisList) > 0";
        TypedQuery<Utilisateur> query = entityManager.createQuery(jpql, Utilisateur.class);
        return query.getResultList();
    }**/


    //////////////////////////..
    @PersistenceContext
    private EntityManager em;

    public List<Utilisateur> findAll() {
        TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class);
        return query.getResultList();
    }

    public List<Utilisateur> findAllByRole(Role role) {
        TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.role = :role", Utilisateur.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    public Utilisateur findById(Long id) {
        return em.find(Utilisateur.class, id);
    }

    public Utilisateur findByEmail(String email) {
        try {
            TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Utilisateur save(Utilisateur utilisateur) {
        if (utilisateur.getId() == null) {
            utilisateur.setCreationDate(LocalDateTime.now());
            utilisateur.setLastConnectionDate(LocalDateTime.now());
            em.persist(utilisateur);
            return utilisateur;
        } else {
            return em.merge(utilisateur);
        }
    }

    public void remove(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            em.remove(utilisateur);
        }
    }


    public List<Utilisateur> searchClients(String searchTerm) {
        String term = "%" + searchTerm.toLowerCase() + "%";
        TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.role = :role AND (LOWER(u.nom) LIKE :term OR LOWER(u.prenom) LIKE :term OR LOWER(u.email) LIKE :term)",
                Utilisateur.class
        );
        query.setParameter("role", Role.CLIENT);
        query.setParameter("term", term);
        return query.getResultList();
    }

    public int countNewClientsWithPackagesSince(LocalDateTime date) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(DISTINCT u) FROM Utilisateur u JOIN u.colis c WHERE u.role = :role AND u.creationDate >= :date AND c.deleted = false",
                Long.class
        );
        query.setParameter("role", Role.CLIENT);
        query.setParameter("date", date);
        return query.getSingleResult().intValue();
    }
    //////////////////
    public List<Utilisateur> findClientsWithPackages() {
        return em.createQuery(
                        "SELECT DISTINCT u FROM Utilisateur u JOIN u.colis c WHERE u.role = :role AND c.deleted = false",
                        Utilisateur.class
                ).setParameter("role", Role.CLIENT)
                .getResultList();
    }

    public List<Utilisateur> searchClientsWithPackages(String searchTerm) {
        String term = "%" + searchTerm.toLowerCase() + "%";
        return em.createQuery(
                        "SELECT DISTINCT u FROM Utilisateur u JOIN u.colis c " +
                                "WHERE u.role = :role AND c.deleted = false AND " +
                                "(LOWER(u.nom) LIKE :term OR LOWER(u.prenom) LIKE :term OR LOWER(u.email) LIKE :term)",
                        Utilisateur.class
                ).setParameter("role", Role.CLIENT)
                .setParameter("term", term)
                .getResultList();
    }

}
