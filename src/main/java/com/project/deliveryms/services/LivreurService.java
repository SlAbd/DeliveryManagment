package com.project.deliveryms.services;

import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.enums.Role;
import com.project.deliveryms.repositories.LivreureRepository;
import com.project.deliveryms.repositories.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Stateless
public class LivreurService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LivreurService.class.getName());

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private LivreureRepository livreurRepository;

    @Inject
    private EmailService emailService;

    public LivreurService() {
        // Constructeur vide requis pour EJB
    }

    @Transactional
    public Livreur createLivreur(String email, String nom, String prenom,
                                 Double latitude, Double longitude, String disponibilite) {
        try {
            String generatedPassword = generateRandomPassword();

            String subject = "Votre compte Livreur - Mot de passe";
            String message = "Bonjour " + prenom + ",\n\n" +
                    "Votre compte livreur a été créé.\n" +
                    "Votre mot de passe est : " + generatedPassword;
            emailService.sendEmail(email, subject, message);

            String hashedPassword = BCrypt.hashpw(generatedPassword, BCrypt.gensalt());

            Utilisateur user = new Utilisateur();
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setMotDePasse(hashedPassword);
            user.setRole(Role.LIVREUR);

            entityManager.persist(user);
            entityManager.flush();

            Livreur livreur = new Livreur();
            livreur.setLatitude(latitude);
            livreur.setLongitude(longitude);
            livreur.setDisponibiliter(disponibilite);
            livreur.setUser(user);

            entityManager.persist(livreur);

            return livreur;

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du livreur : " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updateLivreur(Livreur livreur) {
        try {
            if (livreur == null || livreur.getId() == null) {
                throw new RuntimeException("Données de livreur invalides");
            }
            livreurRepository.update(livreur);
            livreurRepository.getEntityManager().flush();
            LOG.info("Livreur mis à jour avec succès - ID: " + livreur.getId());
        } catch (Exception e) {
            LOG.severe("Erreur lors de la mise à jour: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du livreur: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteLivreur(Long id) {
        Livreur livreur = entityManager.find(Livreur.class, id);
        if (livreur != null) {
            Utilisateur user = livreur.getUser();
            entityManager.remove(livreur);

            if (user != null) {
                entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
            }
        }
    }

    public List<Livreur> getAllLivreurs() {
        return livreurRepository.findAll();
    }

    public List<Livreur> getLivreursIndisponibles() {
        return livreurRepository.findLivreursIndisponibles();
    }

    private String generateRandomPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public Livreur getLivreurById(Long id) {
        try {
            if (id == null) return null;

            Livreur livreur = entityManager.find(Livreur.class, id);

            if (livreur != null && livreur.getUser() != null) {
                livreur.getUser().getNom();
                livreur.getUser().getEmail();
            }
            return livreur;
        } catch (Exception e) {
            LOG.severe("Erreur lors de la récupération du livreur: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Livreur findByEmail(String email) {
        return livreurRepository.findLivreurByEmail(email);
    }
}
