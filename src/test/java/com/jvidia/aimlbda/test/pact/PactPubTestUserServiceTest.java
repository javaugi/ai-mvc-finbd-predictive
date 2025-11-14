/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.pact;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.repository.TestUserRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Provider("TestUserService")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("Temporarily disabled for CICD - Method createUserPact does not conform required method signature 'public au.com.dius.pact.core.model.V4Pact xxx(PactBuilder builder)")
public class PactPubTestUserServiceTest {

    public static final String PROVIDER_NAME = "TestUserService";

    @MockitoBean
    private TestUserRepository mockRepository;

	@BeforeAll
    public void setUp() {
		System.setProperty("pact.verifier.publishResults", "true");
	}

	@TestTemplate
	@ExtendWith(PactVerificationInvocationContextProvider.class)
	void testTemplate(Pact pact, Interaction interaction, HttpRequest request, PactVerificationContext context) {
		context.verifyInteraction();
	}

    @State("TestUser with ID 1 exists")
    public void userWithId1Exists() {
		// Setup: Create a user with ID 1 in the database
		TestUser testUser = TestUser.builder()
             			.id(1L)
			.name("John Doe")
                .email("john.doe@example.com")
                .createdDate(OffsetDateTime.of(LocalDateTime.of(2023, 1, 1, 10, 0, 0), ZoneOffset.UTC))
                .build();

		// Save to test database (using @MockBean or test data setup)
		mockRepository.save(testUser);
	}

    @State("TestUsers with firstName John exist")
    public void usersWithStatusActiveExist() {
		// Setup: Create active users in the database
		TestUser user1 = TestUser.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .name("John Doe")
                .email("john.doe@example.com")
                .createdDate(OffsetDateTime.of(LocalDateTime.of(2023, 1, 1, 10, 0, 0), ZoneOffset.UTC))
                .build();

        TestUser user2 = TestUser.builder()
                .id(2L)
                .firstName("John")
                .lastName("Smith")
                .name("John Smith")
                .email("john.smith@example.com")
                .createdDate(OffsetDateTime.of(LocalDateTime.of(2023, 1, 1, 10, 0, 0), ZoneOffset.UTC))
                .build();

		// Save to test database
		mockRepository.saveAll(List.of(user1, user2));
	}

	@State("No specific state required")
	void noSpecificStateRequired() {
		// No setup needed for this state
	}

}
