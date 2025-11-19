 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
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
public class RestTemplateConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);
    
    @Override
    public void run(String... args) throws Exception {
        log.debug("RestTemplateConfig run args {}", Arrays.toString(args));
    }
    
    public static final String REST_TEMPLATE = "restTemplate";
    public static final String REST_TEMPLATE_SEC = "secureRestTemplate";
    public static final String SECURE_REST_TEMPLATE = "secureRestTemplate";
    public static final String OPEN_AI_API = "openAiApi";
    public static final String OPEN_AI_CHAT_MODEL = "openAiChatModel";
    public static final String OPEN_AI_API_DS = "openAiApiDeepsk";
    public static final String OPEN_AI_CHAT_MODEL_DS = "openAiChatModelDeepsk";
    //Option 1: Run DeepSeek Locally with Ollama (Recommended for Free)
    public static final String OLLAMA_BASE_API = "http://localhost:11434/";
    public static final String OLLAMA_DUMMY_API_KEY = "no-key-needed";
    public static final String OLLAMA_API = OLLAMA_BASE_API + "api/chat";

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
    public RestTemplate secureRestTemplate(RestTemplateBuilder builder)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {

        // Configure SSL for HTTPS
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (chain, authType) -> true) // Trust all certificates (for testing/development)
                // In production, load a specific trust store:
                // .loadTrustMaterial(new File("path/to/truststore.jks"), "password".toCharArray())
                .build();

        // For default hostname verification
        DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext);

        // To customize hostname verification (e.g., disable for testing)
        // DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(
        //     sslContext,
        //     HostnameVerificationPolicy.CLIENT, // Or other policies
        //     NoopHostnameVerifier.INSTANCE // Use carefully, only for testing
        // );
        PoolingHttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy).build();

        org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();

        // Create an HttpComponentsClientHttpRequestFactory using the configured HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Build the RestTemplate with the custom request factory
        return builder
                .requestFactory(() -> requestFactory)
                .build();
    }    
}
