package com.project.deliveryms.beans;

import com.project.deliveryms.entities.BordereauExpedition;
import com.project.deliveryms.entities.Colis;
import com.project.deliveryms.services.BordereauService;
import com.itextpdf.text.DocumentException;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;


import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class BordereauBean implements Serializable {

    @Inject
    private BordereauService bordereauService;

    private BordereauExpedition bordereau = new BordereauExpedition();

    public BordereauBean() {
        // Initialize the colis to avoid null pointer issues
        bordereau.setColis(new Colis());
    }

    public void generateBordereau() throws DocumentException, IOException {
        bordereau.setDateGeneration(LocalDateTime.now()); // ✅ Set the date before generating

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bordereau_" + bordereau.getId() + ".pdf");

        bordereauService.generateBordereauPdf(bordereau, response);
        FacesContext.getCurrentInstance().responseComplete();
    }


    // Getters and Setters
    public BordereauExpedition getBordereau() {
        return bordereau;
    }

    public void setBordereau(BordereauExpedition bordereau) {
        this.bordereau = bordereau;
    }
}
