package com.g04autochef.controller.utils;

import com.g04autochef.model.storableDAO.IngredientRecipe;
import com.g04autochef.model.storableDAO.ShoppingList;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

/**
 * Export a shoppingList to PDF file
 */
public class ShoppingListPDFExporter {

    PdfWriter writer;
    PdfDocument pdf;
    Document document;
    final String name;
    final String fileName;
    HashMap<String, HashMap<String, Vector<IngredientRecipe>>> sortedIngredients;
    final PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    Image pdfImg;


    public ShoppingListPDFExporter(final ShoppingList shoppingList) throws IOException {
        this.name = shoppingList.getName();
        this.fileName = name.replaceAll("\\s", "")+".pdf";
        this.writer = new PdfWriter(this.fileName);
        this.pdf = new PdfDocument(this.writer);
        this.document = new Document(this.pdf);
        this.sortedIngredients = shoppingList.getSortedIngredients();
        ImageData imageData = ImageDataFactory.create(Objects.requireNonNull(getClass().getResource("/com/g04autochef/img/square.png")));
        pdfImg = new Image(imageData);
        pdfImg.setHeight(10);
        pdfImg.setWidth(10);
    }

    /**
     * Export the shoppingList to PDF file
     * @return name of the PDF
     */
    public String exportPDF() throws IOException {
        Paragraph p = new Paragraph(name).setFont(font).setFontSize(20).setBold();
        addEmptyLine(2);
        document.add(p);
        addEmptyLine(3);
        setLogo();
        setContent();
        document.close();
        return fileName;
    }

    private void setLogo() {
        ImageData imageData = ImageDataFactory.create(Objects.requireNonNull(getClass().getResource("/com/g04autochef/img/logo.png")));
        Image image = new Image(imageData);
        image.setWidth(120);
        image.setHeight(120);
        image.setFixedPosition(450, 700);
        document.add(image);
    }

    /**
     * Set shoppingList values in the PDF
     */
    private void setContent() {
        List list;
        HashMap<String, Vector<IngredientRecipe>> ingredientsType;
        String line;
        for (String type : sortedIngredients.keySet()) {
            list = new List().setFont(font).setSymbolIndent(12).setListSymbol(pdfImg);
            ingredientsType = sortedIngredients.get(type);
            for (String unit : ingredientsType.keySet()) {
                for (IngredientRecipe ingr : ingredientsType.get(unit)) {
                    line = ingr.getName() + ", " + ingr.getQuantity() + " " + ingr.getUnitName();
                    if (ingr.getQuantity() > 1) {line += 's';}
                    list.add(new ListItem(line));
                }
            }
            document.add(new Paragraph(type).setFontSize(14).setBold());
            document.add(list);
            addEmptyLine(2);
        }
    }

    private void addEmptyLine(int number) {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(""));
        }
    }
}
