/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.it;

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

public class UserServiceIntegrationTest extends MyApplicationBaseTests {

    @MockitoBean
    private TestUserRepository mockRepository;

    @Autowired
    private TestUserService testUserService;

    @BeforeEach
    public void setUp() {
        // Reset mock to clear previous calls
        reset(mockRepository); // Clear calls from setup/initialization
    }

    @Test
    void testUserCount() {
        // Given
        when(mockRepository.count()).thenReturn(5L);

        // When
        long result = testUserService.getCount();

        // Then
        assertEquals(5, result);
        //Verify
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
        assertEquals(mockUsers.size(), actualUsers.size());
        verify(mockRepository, times(1)).findAll();
    }
}
