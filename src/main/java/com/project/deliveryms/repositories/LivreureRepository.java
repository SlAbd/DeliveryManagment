package com.project.deliveryms.repositories;

import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.entities.User;
import com.project.deliveryms.enums.StatusColis;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless // ou @Repository si Spring
public class LivreureRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    // Insérer un livreur
    public Livreur save(Livreur livreur) {
        entityManager.persist(livreur);
        return livreur;
    }

    // Mettre à jour un livreur
    public void update(Livreur livreur) {
        entityManager.merge(livreur);
    }


    // Supprimer un livreur par ID
    public void delete(Long id) {
        Livreur livreur = find(id);
        if (livreur != null) {
            entityManager.remove(livreur);
        }
    }

    // Trouver un livreur par ID
    public Livreur find(Long id) {
        return entityManager.find(Livreur.class, id);
    }

    // Lister tous les livreurs
    public List<Livreur> findAll() {
        TypedQuery<Livreur> query = entityManager.createQuery("SELECT l FROM Livreur l", Livreur.class);
        return query.getResultList();
    }

    // Trouver un user par email (utile pour vérifier si email existe)
    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst().orElse(null);
    }
    public List<Livreur> findLivreursIndisponibles() {
        TypedQuery<Livreur> query = entityManager.createQuery(
                "SELECT DISTINCT c.livreur FROM Colis c " +
                        "WHERE c.livreur.disponibiliter = 'oui' AND c.status != :statusTermine",
                Livreur.class
        );
        query.setParameter("statusTermine", StatusColis.LIVRE);
        return query.getResultList();
    }




}
