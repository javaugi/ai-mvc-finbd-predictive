/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.service.DataAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final DataAnalysisService dataAnalysisService;

    public AnalysisController(DataAnalysisService dataAnalysisService) {
        this.dataAnalysisService = dataAnalysisService;
    }

    @GetMapping("/summary")
    public String getSummaryReport() {
        return dataAnalysisService.generateSummaryReport();
    }

    ///api/analysis/ask?question=average
    @PostMapping("/ask")
    public String askQuestion(@RequestBody String question) {
        return dataAnalysisService.askQuestionAboutData(question);
    }
    
    @GetMapping("/analyze")
    public String analyzeData(@RequestParam String question) {
        return dataAnalysisService.analyzeDataWithRAG(question);
    }
}