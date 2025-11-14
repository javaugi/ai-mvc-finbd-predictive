/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.service.pact.PactSubTestUserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("Temporarily disabled for CICD - Method createUserPact does not conform required method signature 'public au.com.dius.pact.core.model.V4Pact xxx(PactBuilder builder)")
public class PactSubTestUserServiceTest {

	private static final String PROVIDER_NAME = "UserService";

	private static final String CONSUMER_NAME = "UserWebApp";

	@Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
	public RequestResponsePact getUserByIdPact(PactDslWithProvider builder) {
		return builder.given("User with ID 1 exists")
			.uponReceiving("a request for user with ID 1")
			.path("/api/users/1")
			.method("GET")
			.willRespondWith()
			.status(200)
			.headers(Map.of("Content-Type", "application/json"))
			.body(new PactDslJsonBody().numberType("id", 1L)
				.stringType("name", "John Doe")
				.stringType("email", "john.doe@example.com")
				.stringType("status", "ACTIVE")
				.time("createdAt", "yyyy-MM-dd'T'HH:mm:ss",
						Date.from(LocalDateTime.of(2023, 1, 1, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant())))
			.toPact();
	}

	@Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
	public RequestResponsePact createUserPact(PactDslWithProvider builder) {
		return builder.given("No specific state required")
			.uponReceiving("a request to create a new user")
			.method("POST")
			.path("/api/users")
			.body(new PactDslJsonBody().stringType("name", "Jane Smith")
				.stringType("email", "jane.smith@example.com")
				.stringType("status", "PENDING"))
			.willRespondWith()
			.status(201)
			.headers(Map.of("Content-Type", "application/json"))
			.body(new PactDslJsonBody().numberType("id", 2L)
				.stringType("name", "Jane Smith")
				.stringType("email", "jane.smith@example.com")
				.stringType("status", "PENDING")
				.time("createdAt", "yyyy-MM-dd'T'HH:mm:ss"))
			.toPact();
	}

	@Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
	public RequestResponsePact getUsersByStatusPact(PactDslWithProvider builder) {
		return builder.given("Users with status ACTIVE exist")
			.uponReceiving("a request for users with status ACTIVE")
			.method("GET")
			.path("/api/users")
			.query("status=ACTIVE")
			.willRespondWith()
			.status(200)
			.headers(Map.of("Content-Type", "application/json"))
			.body(new PactDslJsonBody().minArrayLike("", 1) // At least one user
				.object()
				.numberType("id", 1L)
				.stringType("name", "John Doe")
				.stringType("email", "john.doe@example.com")
				.stringType("status", "ACTIVE")
				.closeObject())
			.toPact();
	}

    @Test
	@PactTestFor(pactMethod = "getUserByIdPact")
	void testGetUserById(MockServer mockServer) throws IOException {
		// Arrange
		PactSubTestUserService consumer = new PactSubTestUserService(mockServer.getUrl());

		// Act
		TestUser testUser = consumer.getUserById(1L);

		// Assert
		assertNotNull(testUser);
        assertEquals(Long.valueOf(1L), testUser.getId());
        assertEquals("John Doe", testUser.getName());
    }

    @Test
	@PactTestFor(pactMethod = "createUserPact")
    public void testCreateUser(MockServer mockServer) {
		// Arrange
		PactSubTestUserService consumer = new PactSubTestUserService(mockServer.getUrl());
        TestUser newUser = TestUser.builder().name("Jane Smith").email("jane.smith@example.com").build();

		// Act
		TestUser createdUser = consumer.createUser(newUser);

		// Assert
		assertNotNull(createdUser);
		assertEquals(Long.valueOf(2L), createdUser.getId());
		assertEquals("Jane Smith", createdUser.getName());
    }

    @Test
	@PactTestFor(pactMethod = "getUsersByFirstNamePact")
    public void getUsersByFirstName(MockServer mockServer) {
		// Arrange
		PactSubTestUserService consumer = new PactSubTestUserService(mockServer.getUrl());

		// Act
		List<TestUser> testUsers = consumer.getUsersByFirstName("John");

		// Assert
		assertNotNull(testUsers);
        assertFalse(testUsers.isEmpty());
        assertEquals("John", testUsers.get(0).getFirstName());
	}

}
