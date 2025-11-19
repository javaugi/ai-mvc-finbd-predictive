/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.dto.aiml;

public record OllamaRequest(String model, String prompt) {
    public OllamaRequest(String model) {
        this("llama3", "");
    }

    public OllamaRequest(String model, String prompt) {
        this.model = "llama3";
        this.prompt = "Explain quantum computing";
    }
}
