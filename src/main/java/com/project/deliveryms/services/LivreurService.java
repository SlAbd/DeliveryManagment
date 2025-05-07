/*** package com.project.deliveryms.services;

import com.project.deliveryms.entities.Livreur;
import com.project.deliveryms.websocket.LivreurPositionWebSocket;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing livreur operations, including position updates.
 */
/**@Stateless
public class LivreurService {

    private static final Logger LOGGER = Logger.getLogger(LivreurService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Updates the position of a livreur and broadcasts the update via WebSocket
     * if the livreur has moved more than 100 meters.
     * 
     * @param livreurId The ID of the livreur
     * @param latitude The new latitude
     * @param longitude The new longitude
     * @return true if the update was successful, false otherwise
     */
   /***public boolean updateLivreurPosition(Long livreurId, Double latitude, Double longitude) {
        try {
            // Update the livreur's position in the database
            boolean updated = updateLivreurPositionInDatabase(livreurId, latitude, longitude);

            if (!updated) {
                return false;
            }

            // No need to broadcast via WebSocket anymore as we're using AJAX polling
            // LivreurPositionWebSocket.broadcastPosition(latitude, longitude);

            LOGGER.log(Level.INFO, "Updated position for livreur {0}: lat={1}, lng={2}", 
                    new Object[]{livreurId, latitude, longitude});

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating livreur position", e);
            return false;
        }
    }

    /**
     * Updates the position of a livreur in the database without broadcasting.
     * 
     * @param livreurId The ID of the livreur
     * @param latitude The new latitude
     * @param longitude The new longitude
     * @return true if the update was successful, false otherwise
     */
   /** private boolean updateLivreurPositionInDatabase(Long livreurId, Double latitude, Double longitude) {
        try {
            // Find the livreur
            Livreur livreur = entityManager.find(Livreur.class, livreurId);

            if (livreur == null) {
                LOGGER.log(Level.WARNING, "Livreur with ID {0} not found", livreurId);
                return false;
            }

            // Update the livreur's position
            livreur.setLatitude(latitude);
            livreur.setLongitude(longitude);

            // Save the updated livreur
            entityManager.merge(livreur);

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating livreur position in database", e);
            return false;
        }
    }

    /**
     * Gets a livreur by ID.
     * 
     * @param livreurId The ID of the livreur
     * @return The livreur, or null if not found
     */
    /**public Livreur getLivreurById(Long livreurId) {
        try {
            return entityManager.find(Livreur.class, livreurId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting livreur by ID", e);
            return null;
        }
    }
}*/
