/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfig {
    //*
    public static final String LLM_EMBED_MODEL = "LLM_EMBED_MODEL";
    public static final String LLM_EMBED_STORE = "LLM_EMBED_STORE";

    @Bean(name = LLM_EMBED_MODEL)
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean(name = LLM_EMBED_STORE)
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    } 
    
    /*
    Caused by: org.springframework.beans.factory.support.BeanDefinitionOverrideException: Invalid bean definition with name 'chatClientBuilderConfigurer' 
    defined in class path resource [org/springframework/ai/model/chat/client/autoconfigure/ChatClientAutoConfiguration.class]: Cannot register bean 
    definition [Root bean: class=null; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true; primary=false;
    fallback=false; factoryBeanName=org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
    factoryMethodName=chatClientBuilderConfigurer; initMethodNames=null; destroyMethodNames=[(inferred)]; defined in class path 
    resource [org/springframework/ai/model/chat/client/autoconfigure/ChatClientAutoConfiguration.class]] for bean 'chatClientBuilderConfigurer' 
    since there is already [Root bean: class=null; scope=; abstract=false; lazyInit=null; autowireMode=3; dependencyCheck=0; autowireCandidate=true;
    primary=false; fallback=false; factoryBeanName=org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration; 
    factoryMethodName=chatClientBuilderConfigurer; initMethodNames=null; destroyMethodNames=[(inferred)]; defined in class path resource 
    [org/springframework/ai/autoconfigure/chat/client/ChatClientAutoConfiguration.class]] bound.

    // */
}