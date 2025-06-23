package com.project.deliveryms.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.entities.Utilisateur;
import com.project.deliveryms.services.ColisService;
import com.project.deliveryms.services.UtilisateurService;

@Named
@ViewScoped
public class ClientWithPackagesController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UtilisateurService utilisateurService;

    @Inject
    private ColisService colisService;

    private List<Utilisateur> clientsWithPackages;
    private String searchTerm;
    private Map<Long, Integer> packageCountsByClient;
    private int clientsWithPackagesCount;
    private int activeColis;
    private int newClientsCount;
    private double averagePackagesPerClient;
    private int clientsWithSinglePackage;
    private List<Utilisateur> top5Clients;

    @PostConstruct
    public void init() {
        loadClientsWithPackages();
        calculateStatistics();
    }

    public void loadClientsWithPackages() {
        clientsWithPackages = utilisateurService.findClientsWithPackages();

        List<Colis> allColis = colisService.findAllNonDeleted();

        // Initialiser le nombre de colis par client
        packageCountsByClient = new HashMap<>();
        for (Colis colis : allColis) {
            if (colis.getUtilisateur() != null) {
                Long clientId = colis.getUtilisateur().getId();
                packageCountsByClient.put(clientId, packageCountsByClient.getOrDefault(clientId, 0) + 1);
            }
        }
    }

    public void calculateStatistics() {
        clientsWithPackagesCount = clientsWithPackages.size();

        List<Colis> allColis = colisService.findAllNonDeleted();
        activeColis = allColis.size();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        newClientsCount = (int) clientsWithPackages.stream()
                .filter(client -> client.getCreationDate() != null && client.getCreationDate().isAfter(sevenDaysAgo))
                .count();

        if (!clientsWithPackages.isEmpty()) {
            averagePackagesPerClient = (double) activeColis / clientsWithPackagesCount;
        } else {
            averagePackagesPerClient = 0.0;
        }

        clientsWithSinglePackage = (int) clientsWithPackages.stream()
                .filter(client -> getPackageCount(client.getId()) == 1)
                .count();

        top5Clients = clientsWithPackages.stream()
                .sorted((u1, u2) -> Integer.compare(
                        getPackageCount(u2.getId()), getPackageCount(u1.getId())))
                .limit(5)
                .collect(Collectors.toList());
    }

    public void searchClientsWithPackages() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadClientsWithPackages();
        } else {
            clientsWithPackages = utilisateurService.searchClientsWithPackages(searchTerm);
        }
        calculateStatistics();
    }

    public int getPackageCount(Long clientId) {
        return packageCountsByClient.getOrDefault(clientId, 0);
    }

    // Getters & Setters
    public List<Utilisateur> getClientsWithPackages() {
        return clientsWithPackages;
    }

    public void setClientsWithPackages(List<Utilisateur> clientsWithPackages) {
        this.clientsWithPackages = clientsWithPackages;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getClientsWithPackagesCount() {
        return clientsWithPackagesCount;
    }

    public int getActiveColis() {
        return activeColis;
    }

    public int getNewClientsCount() {
        return newClientsCount;
    }

    public double getAveragePackagesPerClient() {
        return averagePackagesPerClient;
    }

    public int getClientsWithSinglePackage() {
        return clientsWithSinglePackage;
    }

    public List<Utilisateur> getTop5Clients() {
        return top5Clients;
    }
    public Date getConvertedCreationDate(Utilisateur client) {
        LocalDateTime localDateTime = client.getCreationDate(); // ou autre type
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}
