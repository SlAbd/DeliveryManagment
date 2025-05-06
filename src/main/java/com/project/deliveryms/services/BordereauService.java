package com.project.deliveryms.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.deliveryms.entities.BordereauExpedition;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@RequestScoped
public class BordereauService {

    public void generateBordereauPdf(BordereauExpedition bordereau, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Add content to the document (bordereau details)
        document.add(new Paragraph("Bordereau d'expédition"));
        document.add(new Paragraph("ID: " + bordereau.getId()));
        document.add(new Paragraph("Colis: " + bordereau.getColis().getNumeroSuivi())); // <-- corrected here
        document.add(new Paragraph("Date de génération: " + bordereau.getDateGeneration().toString()));

        // Add more details if needed

        document.close();
    }
}
