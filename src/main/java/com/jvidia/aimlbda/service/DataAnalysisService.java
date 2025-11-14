/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service;

import static com.jvidia.aimlbda.config.OllamaConfig.OLLAMA_CHAT_MODEL;
import com.jvidia.aimlbda.entity.AuditLog;
import dev.langchain4j.data.document.Document;
//import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;
import dev.langchain4j.data.document.Metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.jvidia.aimlbda.repository.AuditLogRepository;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
//@RequiredArgsConstructor(onConstructor = @__(
//        @Autowired))
public class DataAnalysisService {

    private final AuditLogRepository auditLogRepository;
    private final @Qualifier(OLLAMA_CHAT_MODEL)
    ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public DataAnalysisService(AuditLogRepository auditLogRepository, @Qualifier(OLLAMA_CHAT_MODEL) ChatModel chatModel,
             EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.auditLogRepository = auditLogRepository;
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public interface Analyst {

        String analyzeData(String question);
    }

    public String analyzeDataWithRAG(String question) {
        // Step 1: Retrieve relevant data from database
        List<AuditLog> relevantData = retrieveRelevantData(question);

        // Step 2: Create documents from the data
        List<Document> documents = createDocumentsFromData(relevantData);

        // Step 3: Split documents into segments and embed them
        embedDocuments(documents);

        // Step 4: Create a retriever
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.from(embeddingStore);
        //EmbeddingStoreContentRetriever.from(embeddingStore, embeddingModel);

        // Step 5: Create an AI service with RAG
        Analyst analyst = AiServices.builder(Analyst.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                //.retriever(retriever)
                .build();

        // Step 6: Analyze the data
        return analyst.analyzeData(question);
    }

    private List<AuditLog> retrieveRelevantData(String question) {
        // Simple keyword-based retrieval - enhance with more sophisticated logic
        if (question.toLowerCase().contains("category")) {
            return auditLogRepository.findAll();
        } else if (question.toLowerCase().contains("date") || question.toLowerCase().contains("time")  || question.toLowerCase().contains("auditDate")) {
            return auditLogRepository.findByDateRange(Instant.now().minus(3, ChronoUnit.MONTHS), Instant.now());
        }
        return auditLogRepository.findAll();
    }

    private List<Document> createDocumentsFromData1(List<AuditLog> data) {
        return data.stream()
                .map(entity -> String.format(
                "Category: %s, Value: %.2f, Date: %s, Region: %s, Description: %s",
                entity.getCategory(),
                entity.getAuditValue(),
                entity.getAuditDate(),
                entity.getRegion(),
                entity.getDescription()
        ))
                .map(text -> Document.from(text))
                .collect(Collectors.toList());
    }

    private List<Document> createDocumentsFromData2(List<AuditLog> data) {
        return data.stream()
                .map(entity -> {
                    String text = String.format(
                            "Category: %s, Value: %.2f, Date: %s, Region: %s, Description: %s",
                            entity.getCategory(),
                            entity.getAuditValue(),
                            entity.getAuditDate(),
                            entity.getRegion(),
                            entity.getDescription()
                    );

                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("category", entity.getCategory());
                    metadata.put("region", entity.getRegion());
                    metadata.put("auditDate", entity.getAuditDate().toString());
                    metadata.put("auditValue", entity.getAuditValue().toString());

                    return Document.from(text, new Metadata(metadata));
                })
                .collect(Collectors.toList());
    }

    private List<Document> createDocumentsFromData(List<AuditLog> data) {
        return data.stream()
                .map(entity -> {
                    // Create text content
                    String text = String.format(
                            "Data Record:\n"
                            + "Category: %s\n"
                            + "Value: %.2f\n"
                            + "Date: %s\n"
                            + "Region: %s\n"
                            + "Description: %s",
                            entity.getCategory(),
                            entity.getAuditValue(),
                            entity.getAuditDate(),
                            entity.getRegion(),
                            entity.getDescription()
                    );

                    // Create metadata
                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("id", entity.getId().toString());
                    metadata.put("category", entity.getCategory());
                    metadata.put("region", entity.getRegion());
                    metadata.put("auditDate", entity.getAuditDate().toString());
                    
            return Document.from(text, new Metadata(metadata));
                })
                .collect(Collectors.toList());
    }

    private void embedDocuments(List<Document> documents) {
        documents.forEach(document -> {
            // Split document into segments
            List<TextSegment> segments = DocumentSplitters.recursive(300, 0).split(document);

            // Embed each segment
            segments.forEach(segment -> {
                Response<Embedding> embeddingResponse = embeddingModel.embed(segment.text());
                embeddingStore.add(embeddingResponse.content(), segment);
            });
        });
    }

    public String generateSummaryReport() {
        List<AuditLog> allData = auditLogRepository.findAll();

        // Prepare data for the prompt
        Map<String, Object> variables = new HashMap<>();
        variables.put("dataCount", allData.size());
        variables.put("categories", allData.stream()
                .map(AuditLog::getCategory)
                .distinct()
                .collect(Collectors.toList()));
        variables.put("averageValue", allData.stream()
                .mapToDouble(AuditLog::getAuditValue)
                .average()
                .orElse(0.0));

        // Create a prompt template
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Generate a comprehensive summary report based on the following data:\n"
                + "Total records: {{dataCount}}\n"
                + "Categories: {{categories}}\n"
                + "Average value: {{averageValue}}\n\n"
                + "Please provide insights, trends, and any notable patterns in the data."
        );

        Prompt prompt = promptTemplate.apply(variables);
        return chatModel.chat(prompt.text());
    }

    public String askQuestionAboutData(String question) {
        // First try to answer with direct data lookup
        if (question.toLowerCase().contains("average")) {
            double average = auditLogRepository.findAll().stream()
                    .mapToDouble(AuditLog::getAuditValue)
                    .average()
                    .orElse(0.0);
            return String.format("The average value is %.2f", average);
        }

        // For more complex questions, use the LLM with RAG
        return analyzeDataWithRAG(question);
    }
}
