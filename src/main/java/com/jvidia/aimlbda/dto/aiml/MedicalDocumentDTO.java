/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.dto.aiml;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicalDocumentDTO {

    private Long id;

    private Long version; // For optimistic locking

    private String title;

    private String textContent;

    private String specialty;

    private String documentType;

    private OffsetDateTime publicationDate;

    private float[] embedding;

    private byte[] pdfContent;

    public void setEmbeddingFromList(List<Double> embeddingList) {
        this.embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            this.embedding[i] = embeddingList.get(i).floatValue();
        }
    }
}
