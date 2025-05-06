package com.project.deliveryms.beans;

import com.project.deliveryms.entities.BordereauExpedition;
import com.project.deliveryms.services.BordereauService;
import com.itextpdf.text.DocumentException;
import jakarta.inject.Named; // Updated import for Named annotation
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;

@Named // Replaced @ManagedBean with @Named
@SessionScoped
public class BordereauBean implements Serializable {

    @Inject
    private BordereauService bordereauService;

    private BordereauExpedition bordereau = new BordereauExpedition(); // Always good to initialize

    public void generateBordereau() throws DocumentException, IOException {
        // Prepare response
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bordereau_" + bordereau.getId() + ".pdf");

        // Generate the PDF
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
