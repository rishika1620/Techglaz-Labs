package com.example.radha.techglaz;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PdfData extends RealmObject {
    @PrimaryKey
    private String id;
    private String pdfContent; // Store the content of the PDF as a string or in an appropriate format

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(String pdfContent) {
        this.pdfContent = pdfContent;
    }
}
