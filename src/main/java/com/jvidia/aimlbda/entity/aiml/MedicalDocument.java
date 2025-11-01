/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.entity.aiml;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data
@Builder(toBuilder = true)
@Table(name = "medicalDocuments")
public class MedicalDocument {

    @Id
    private String id;

    @Version
    private Long version; // For optimistic locking

    private String title;

    @Column(name = "text_content")
    private String textContent;

    private String specialty;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "publication_date")
    private OffsetDateTime publicationDate;

    @Column(name = "embedding")
    private float[] embedding;

    // For file content, we'll handle separately
    @Column(name = "pdf_content")
    private byte[] pdfContent;

    public void setEmbeddingFromList(List<Double> embeddingList) {
        this.embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            this.embedding[i] = embeddingList.get(i).floatValue();
        }
    }
}
