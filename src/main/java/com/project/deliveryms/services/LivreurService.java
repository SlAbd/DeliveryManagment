package com.project.deliveryms.services;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.entities.User;
import com.project.deliveryms.enums.Role;
import com.project.deliveryms.enums.StatusColis;
import com.project.deliveryms.repositories.ColisRepository;
import com.project.deliveryms.repositories.LivreureRepository;
import com.project.deliveryms.repositories.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.mail.MessagingException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Random;

@Stateless
public class LivreurService {

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

    /**
     * Crée un nouveau livreur avec un compte utilisateur associé
     */

    @Transactional
    public Livreur createLivreur(String email, String prenom, String nom,
                                 Double latitude, Double longitude, String disponibilite) {
        try {
            // 1. Générer un mot de passe aléatoire
            String generatedPassword = generateRandomPassword();

            // 2. Envoyer l'email
            String subject = "Votre compte Livreur - Mot de passe";
            String message = "Bonjour " + prenom + ",\n\n" +
                    "Votre compte livreur a été créé.\n" +
                    "Votre mot de passe est : " + generatedPassword;
            emailService.sendEmail(email, subject, message);

            // 3. Hacher le mot de passe
            String hashedPassword = BCrypt.hashpw(generatedPassword, BCrypt.gensalt());

            // 4. Créer et persister l'utilisateur
            User user = new User();
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setMotDePasse(hashedPassword);
            user.setRole(Role.LIVREUR);

            entityManager.persist(user);
            entityManager.flush(); // Pour générer l'id du user

            // 5. Créer et persister le livreur lié à l'utilisateur
            Livreur livreur = new Livreur();

            livreur.setLatitude(latitude);
            livreur.setLongitude(longitude);
            livreur.setDisponibiliter(disponibilite);
            livreur.setUser(user); // Association directe

            entityManager.persist(livreur);

            return livreur;

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du livreur : " + e.getMessage(), e);
        }
    }

    /**
     * Génère un mot de passe aléatoire sécurisé
     */
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

    /**
     * Récupérer tous les livreurs
     */
    public List<Livreur> getAllLivreurs() {
        return livreurRepository.findAll();
    }


    // Méthode pour mettre à jour un livreur
    // Méthode pour mettre à jour un livreur




    @Transactional
    public void deleteLivreur(Long id) {
        Livreur livreur = entityManager.find(Livreur.class, id);
        if (livreur != null) {
            User user = livreur.getUser();

            // Supprimer d'abord le livreur
            entityManager.remove(livreur);

            // Ensuite, supprimer l'utilisateur s'il n'est pas utilisé ailleurs
            if (user != null) {
                entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
            }
        }
    }

    @Transactional
    public void updateLivreur(Livreur livreur) {
        try {
            // Récupérer le livreur existant depuis la base de données
            Livreur existingLivreur = entityManager.find(Livreur.class, livreur.getId());
            if (existingLivreur != null) {
                // Mettre à jour les propriétés du livreur
                existingLivreur.setDisponibiliter(livreur.getDisponibiliter());

                // Si les coordonnées sont définies dans le livreur passé en paramètre
                if (livreur.getLatitude() != null) {
                    existingLivreur.setLatitude(livreur.getLatitude());
                }
                if (livreur.getLongitude() != null) {
                    existingLivreur.setLongitude(livreur.getLongitude());
                }

                // Mettre à jour les détails de l'utilisateur
                User existingUser = existingLivreur.getUser();
                if (existingUser != null && livreur.getUser() != null) {
                    existingUser.setNom(livreur.getUser().getNom());
                    existingUser.setPrenom(livreur.getUser().getPrenom());
                    existingUser.setEmail(livreur.getUser().getEmail());

                    // Mettre à jour l'utilisateur
                    entityManager.merge(existingUser);
                }

                // Mettre à jour le livreur
                entityManager.merge(existingLivreur);
                entityManager.flush(); // S'assurer que les modifications sont écrites en base
            } else {
                throw new RuntimeException("Livreur avec ID " + livreur.getId() + " non trouvé");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du livreur: " + e.getMessage(), e);
        }
    }


    // In LivreurService class
    public List<Livreur> getLivreursIndisponibles() {
        return livreurRepository.findLivreursIndisponibles(); // Use the method in the repository
    }


}

