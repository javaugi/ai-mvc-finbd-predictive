package com.jvidia.aimlbda;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test") // Default profile for all extending tests
@ContextConfiguration(classes = MyApplicationTestConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public abstract class MyApplicationBaseTests {
    @Value("${spring.datasource.url}")
    public String dataSourceUrl;
    @Value("${spring.datasource.username}")
    public String dataSourceUsername;
    @Value("${spring.datasource.password}")
    public String datasourcePwd;

    @LocalServerPort
    public int port;

    public static final String HEALTH_URL = "/actuator/health";

}
