/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.container;

import com.jvidia.aimlbda.entity.TestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/*
Best Practices
    Use @DataJpaTest for repository layer tests with H2
    Use Testcontainers for integration tests that need real database behavior
    Reuse containers between tests when possible (declare containers as static)
    Clean up data after each test with @Transactional or manual cleanup
    Consider test profiles to separate different test configurations
 */
@ActiveProfiles("test") // Default profile for all extending tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
@Disabled("Temporarily disabled for CICD - it needs Docker running")
public class TestRestTemplatePostgreSQL {

	@Container
	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withDatabaseName("testdb")
		.withUsername("postgres")
		.withPassword("admin");

	@DynamicPropertySource
	public static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void shouldCreateAndGetCustomer() {
		// Create customer
		TestUser testuser = TestUser.builder()
                .enabled(true)
                .name("API Test").email("api@test.com").build();
        ResponseEntity<TestUser> createResponse = restTemplate.postForEntity("/api/testonly", testuser,
                TestUser.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Get customer
		Long testUserId = createResponse.getBody().getId();
        ResponseEntity<TestUser> getResponse = restTemplate.getForEntity("/api/testonly/" + testUserId,
                TestUser.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody().getEmail()).isEqualTo("api@test.com");
	}

}
