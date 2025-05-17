package com.project.deliveryms.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.project.deliveryms.entities.BordereauExpedition;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;


// Add Apache Commons IO dependency if needed


import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Named
@RequestScoped
public class BordereauService {

    public void generateBordereauPdf(BordereauExpedition bordereau, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Add logo image (truck icon)
        InputStream logoStream = getClass().getClassLoader().getResourceAsStream("images/truck_logo.png");
        if (logoStream != null) {
            Image logo = Image.getInstance(IOUtils.toByteArray(logoStream));
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.ALIGN_RIGHT);
            document.add(logo);
        } else {
            document.add(new Paragraph("Logo non disponible."));
        }


        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("BON DE RÉCEPTION DE COMMANDE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Format date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        table.addCell("N° de suivi :");
        table.addCell(bordereau.getColis().getNumeroSuivi());
        table.addCell("Description :");
        table.addCell(bordereau.getColis().getDescription());
        table.addCell("Poids :");
        table.addCell(bordereau.getColis().getPoids() + " kg");

        // Date d'envoi check
        table.addCell("Date d’envoi :");
        if (bordereau.getColis().getDateEnvoi() != null) {
            table.addCell(bordereau.getColis().getDateEnvoi().format(formatter));
        } else {
            table.addCell("Non disponible");
        }

        // Date de livraison check
        table.addCell("Date de livraison :");
        if (bordereau.getColis().getDateLivraison() != null) {
            table.addCell(bordereau.getColis().getDateLivraison().format(formatter));
        } else {
            table.addCell("Non disponible");
        }

        // Statut check for null
        table.addCell("Statut :");
        if (bordereau.getColis().getStatus() != null) {
            table.addCell(bordereau.getColis().getStatus().toString());
        } else {
            table.addCell("Statut non disponible");
        }

        // Date de génération check
        table.addCell("Date de génération :");
        if (bordereau.getDateGeneration() != null) {
            table.addCell(bordereau.getDateGeneration().format(formatter));
        } else {
            table.addCell("Non disponible");
        }

        document.add(table);

        // Footer
        document.add(new Paragraph("\nCommentaires : ................................................................."));
        document.add(new Paragraph("Dispositions à prendre : ....................................................."));

        document.add(new Paragraph("\n\nCommande complète : ______    Reçue par : ____________    Date : ____________"));

        document.close();
    }
}
