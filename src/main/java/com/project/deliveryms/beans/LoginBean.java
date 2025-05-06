package com.project.deliveryms.beans;

import com.project.deliveryms.entities.Utilisateur;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import com.project.deliveryms.services.UtilisateurService;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Utilisateur utilisateur;
    private String email;
    private String motDePasse;

    @Inject
    private UtilisateurService utilisateurService; // Injection du service

    public String login() {
        String result = utilisateurService.authentifier(email, motDePasse);

        System.out.println("Résultat authentification: " + result); // Débogage

        if ("Connexion réussie".equals(result)) {
            // L'authentification est réussie, vous récupérez l'utilisateur par son email
            utilisateur = utilisateurService.findUserByEmail(email);
            if (utilisateur != null) {
                System.out.println("Utilisateur récupéré : " + utilisateur.getPrenom() + " " + utilisateur.getNom());
            } else {
                System.out.println("Utilisateur non trouvé après authentification.");
            }
            return "/pages/dashboard.xhtml?faces-redirect=true";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, result, null));
            System.out.println("Message ajouté: " + result); // Débogage
            return null; // Reste sur la même page sans redirection
        }
    }

    // Getters et setters
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    public void logout() {
        // Implement the logout functionality, such as invalidating the session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        // Optionally, redirect to a different page, like the login page
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
