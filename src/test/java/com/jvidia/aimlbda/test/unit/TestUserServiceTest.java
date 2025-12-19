/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.unit;

// --- The Unit Test ---
import com.jvidia.aimlbda.MyApplicationBaseTests;
import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.repository.TestUserRepository;
import com.jvidia.aimlbda.service.TestUserService;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class TestUserServiceTest extends MyApplicationBaseTests {
    @MockitoBean // Creates a mock object of the repository
    private TestUserRepository mockRepository;

    // Injects the mock into the service instance
    //@InjectMocks
    @Autowired
    private TestUserService testUserService;

    @BeforeEach
    public void setUp() {
        // Reset mock to clear previous calls
        //reset(mockRepository); // Clear calls from setup/initialization
    }

    @Test
    void testUserCount() {
        // Reset mock to clear previous calls
        reset(mockRepository); // Clear calls from setup/initialization
        // Arrange: Define the behavior of the mocked dependency
        when(mockRepository.count()).thenReturn(5L);

        // Act: Call the method on the service
        long count = testUserService.getCount();

        // Assert: Verify the result and the interaction with the mock
        assertEquals(5, count, "The count should match the mocked value.");

        // Verify: Ensure the repository's count() method was called exactly once
        verify(mockRepository, times(1)).count();
    }

    @Test
    void shouldReturnMockedUsers() {
        // Arrange
        List<TestUser> mockUsers = Arrays.asList(
                TestUser.builder().name("user1").email("user1@test.com").build(),
                TestUser.builder().name("user2").email("user2@test.com").build(),
                TestUser.builder().name("user3").email("user3@test.com").build(),
                TestUser.builder().name("user4").email("user4@test.com").build(),
                TestUser.builder().name("user5").email("user5@test.com").build()
        );

        when(mockRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<TestUser> actualUsers = testUserService.getAll();

        // Assert
        assertEquals(5, actualUsers.size());
        verify(mockRepository, times(1)).findAll();
    }
}
