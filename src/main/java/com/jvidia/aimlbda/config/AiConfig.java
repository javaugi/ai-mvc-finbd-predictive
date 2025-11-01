 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import java.util.Arrays;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author javaugi
 */
@Component
@Configuration
public class AiConfig implements CommandLineRunner{
    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);
    
    @Override
    public void run(String... args) throws Exception {
        log.info("AiDeepSeekConfig with args {}", Arrays.toString(args)); 
    }
    
    public static final String REST_TEMPLATE = "restTemplate";
    public static final String REST_TEMPLATE_SEC = "secureRestTemplate";
    public static final String SECURE_REST_TEMPLATE = "secureRestTemplate";
    public static final String OPEN_AI_API = "openAiApi";
    public static final String OPEN_AI_CHAT_MODEL = "openAiChatModel";
    public static final String OPEN_AI_API_DS = "openAiApiDeepsk";
    public static final String OPEN_AI_CHAT_MODEL_DS = "openAiChatModelDeepsk";
    //Option 1: Run DeepSeek Locally with Ollama (Recommended for Free)
    public static final String OLLAMA_BASE_API = "http://localhost:11434/v1/";
    public static final String OLLAMA_DUMMY_API_KEY = "no-key-needed";
    public static final String OLLAMA_API = OLLAMA_BASE_API + "generate";

    @Primary
    @Bean(name = REST_TEMPLATE)
    public RestTemplate restTemplate()  {
        return new RestTemplate();
    }
    
    @Bean
    public RestTemplateBuilder builder() {
        return new RestTemplateBuilder();
    }
    
    @Bean(name = REST_TEMPLATE_SEC)
    public RestTemplate secureRestTemplate(RestTemplateBuilder builder) throws Exception {

        // This configuration allows your application to skip the SSL check
        final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        final SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        final SSLConnectionSocketFactory sslsf =
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory> create()
                        .register("https", sslsf)
                        .register("http", new PlainConnectionSocketFactory())
                        .build();

        final BasicHttpClientConnectionManager connectionManager =
                new BasicHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
                .build();
    }    
}
