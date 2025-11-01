/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.repository.aiml;

import com.jvidia.aimlbda.entity.aiml.MedicalDocument;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, String> {

    @Query("""
        SELECT id, title, text_content, specialty, document_type, publication_date,
               embeddding
        FROM medicalDocuments
        ORDER BY embeddding <=> $1
        LIMIT 3
        """)
    List<MedicalDocument> findSimilarDocuments(float[] embedding);

    @Query("""
        SELECT id, title, text_content, specialty, document_type, publication_date,
               embeddding
        FROM medicalDocuments
        ORDER BY embeddding <=> $1
        LIMIT $2
        """)
    List<MedicalDocument> findSimilarDocuments(float[] embedding, Integer limit);

    @Query("""
        INSERT INTO medicalDocuments
        (title, text_content, specialty, document_type, publication_date, embeddding)
        VALUES ($1, $2, $3, $4, $5, $6)
        RETURNING id
        """)
    Long saveDocument(
            String title,
        String textContent,
        String specialty,
        String documentType,
        OffsetDateTime publicationDate,
        float[] embedding
    );

    @Query("""
        SELECT * FROM medical_documents
        WHERE id = $1
        FOR UPDATE SKIP LOCKED
        """)
    MedicalDocument findByIdForUpdate(String id);

    @Query("""
        SELECT * FROM medical_documents
        WHERE id = $1
        FOR UPDATE NOWAIT
        """)
    MedicalDocument findByIdForUpdateNoWait(String id);
}
