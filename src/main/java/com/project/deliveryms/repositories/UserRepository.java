package com.project.deliveryms.repositories;

import com.project.deliveryms.entities.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    public void save(User user) {
        entityManager.persist(user);
    }

    public Optional<User> findById(Long id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    public Optional<User> findByEmail(String email) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();

        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    public void update(User user) {
        entityManager.merge(user);
    }

    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }
}
