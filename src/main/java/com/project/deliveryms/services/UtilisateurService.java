package com.project.deliveryms.services;

import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.Role;
import com.project.deliveryms.repositories.LivreureRepository;
import com.project.deliveryms.repositories.UtilisateurRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;

@Named
@RequestScoped
public class UtilisateurService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UtilisateurRepository utilisateurRepository;

    @Inject
    private LivreureRepository livreurRepository;

    // ======================
    // Authentification / Inscription
    // ======================

    public String authentifier(String email, String motDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur == null) return "Utilisateur non trouvé";
        if (!BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse())) return "Mot de passe incorrect";
        return "Connexion réussie";
    }

    @Transactional
    public String inscrire(Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()) != null)
            return "Email déjà utilisé. Veuillez en choisir un autre.";

        utilisateur.setCreationDate(LocalDateTime.now());
        utilisateur.setLastConnectionDate(LocalDateTime.now());
        String hashedPwd = BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
        utilisateur.setMotDePasse(hashedPwd);

        utilisateurRepository.save(utilisateur);
        return "Inscription réussie";
    }

    // ======================
    // Méthodes CRUD
    // ======================

    public Utilisateur findUserByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public Utilisateur findById(Long id) {
        return entityManager.find(Utilisateur.class, id);
    }

    @Transactional
    public void update(Utilisateur utilisateur) {
        Utilisateur existingUser = entityManager.find(Utilisateur.class, utilisateur.getId());
        if (existingUser != null) {
            existingUser.setNom(utilisateur.getNom());
            existingUser.setPrenom(utilisateur.getPrenom());
            existingUser.setEmail(utilisateur.getEmail());
            if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
                existingUser.setMotDePasse(BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt()));
            }
        }
    }

    public void remove(Long id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) entityManager.remove(utilisateur);
    }

    public List<Utilisateur> findAll() {
        return entityManager.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class).getResultList();
    }

    public List<Utilisateur> findAllByRole(Role role) {
        TypedQuery<Utilisateur> query = entityManager.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.role = :role", Utilisateur.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    // ======================
    // Méthodes pour Client et Colis
    // ======================

    public List<Utilisateur> findClientsWithPackages() {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM Utilisateur u JOIN u.colis c WHERE u.role = :role AND c.deleted = false",
                        Utilisateur.class)
                .setParameter("role", Role.CLIENT)
                .getResultList();
    }

    public List<Utilisateur> searchClientsWithPackages(String searchTerm) {
        String term = "%" + searchTerm.toLowerCase() + "%";
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM Utilisateur u JOIN u.colis c " +
                                "WHERE u.role = :role AND c.deleted = false AND " +
                                "(LOWER(u.nom) LIKE :term OR LOWER(u.prenom) LIKE :term OR LOWER(u.email) LIKE :term)",
                        Utilisateur.class)
                .setParameter("role", Role.CLIENT)
                .setParameter("term", term)
                .getResultList();
    }

    public List<Utilisateur> searchClients(String searchTerm) {
        String term = "%" + searchTerm.toLowerCase() + "%";
        return entityManager.createQuery(
                        "SELECT u FROM Utilisateur u WHERE u.role = :role AND " +
                                "(LOWER(u.nom) LIKE :term OR LOWER(u.prenom) LIKE :term OR LOWER(u.email) LIKE :term)",
                        Utilisateur.class)
                .setParameter("role", Role.CLIENT)
                .setParameter("term", term)
                .getResultList();
    }

    public int countNewClientsWithPackagesSince(LocalDateTime date) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(DISTINCT u) FROM Utilisateur u JOIN u.colis c " +
                        "WHERE u.role = :role AND u.creationDate >= :date AND c.deleted = false", Long.class);
        query.setParameter("role", Role.CLIENT);
        query.setParameter("date", date);
        return query.getSingleResult().intValue();
    }

    // ======================
    // Méthodes pour session utilisateur / livreur
    // ======================

    public Utilisateur getUtilisateurConnecte() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        return (Utilisateur) session.getAttribute("utilisateurConnecte");
    }

    public Livreur getLivreurConnecte() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session == null) return null;

        Object utilisateurSession = session.getAttribute("utilisateurConnecte");

        if (utilisateurSession instanceof Livreur) {
            return (Livreur) utilisateurSession;
        } else if (utilisateurSession instanceof Utilisateur utilisateur) {
            return livreurRepository.findLivreurByEmail(utilisateur.getEmail());
        }

        return null;
    }

    public Utilisateur getUtilisateurByEmail(String email) {
        return livreurRepository.findByEmail(email);
    }
}
