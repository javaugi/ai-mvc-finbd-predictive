/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test.mockmvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvidia.aimlbda.MyApplicationBaseTests;
import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.repository.TestUserRepository;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isA;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUserRestTests extends MyApplicationBaseTests {

    private static final String BASE_REST_URI = "/api/testonly";

    private static final String BASE_REST_URI_W_ID = "/api/testonly/";

	@Autowired
    private TestUserRepository testUserRepository;

	@Autowired
	private MockMvc mockMvc;

	private static final ObjectMapper om = new ObjectMapper();

	@BeforeEach
	public void setup() {
        testUserRepository.deleteAll();
	}

    private TestUser createTestUser() {
        return TestUser.builder()
                .enabled(true)
                .name("API Test").email("api@test.com").build();
	}

	@Test
    public void testPost() throws Exception {
        TestUser sampleTestUser = createTestUser();
        TestUser actualRecord = om.readValue(mockMvc
             			.perform(post(BASE_REST_URI).contentType("application/json")
                        .content(om.writeValueAsString(sampleTestUser)))
             			.andDo(print())
			.andExpect(jsonPath("$.id", greaterThan(0)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
                .getContentAsString(), TestUser.class);
        assertTrue(testUserRepository.findById(actualRecord.getId()).isPresent());
	}

	@Test
    public void testPostGetVerify() throws Exception {
        TestUser sampleTestUser = createTestUser();

		int n = 5;
		for (int i = 0; i < 5; i++) {
			om.readValue(mockMvc
				.perform(post(BASE_REST_URI).contentType("application/json")
                            .content(om.writeValueAsString(sampleTestUser)))
                				.andDo(print())
				.andExpect(jsonPath("$.id", greaterThan(0)))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
                    .getContentAsString(), TestUser.class);
		}

        List<TestUser> actualResult = om.readValue(mockMvc.perform(get(BASE_REST_URI))
             			.andDo(print())
			.andExpect(jsonPath("$.*", isA(ArrayList.class)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
                .getContentAsString(), new TypeReference<List<TestUser>>() {
			});

		assertEquals(n, actualResult.size());
	}

	@Test
    public void testGetEmpty() throws Exception {
		mockMvc.perform(get(BASE_REST_URI))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
    public void testPostDeleteVerify() throws Exception {
        TestUser sampleTestUser = createTestUser();
        TestUser expectedRecord = om.readValue(mockMvc
             			.perform(post(BASE_REST_URI).contentType("application/json")
                        .content(om.writeValueAsString(sampleTestUser)))
             			.andDo(print())
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
                .getContentAsString(), TestUser.class);

		mockMvc.perform(delete(BASE_REST_URI_W_ID + expectedRecord.getId()).contentType("application/json"))
			.andDo(print())
			.andExpect(status().isNoContent());

        assertFalse(testUserRepository.findById(expectedRecord.getId()).isPresent());
	}

	@Test
    public void testPostGetVerifyById() throws Exception {
        TestUser sampleTestUser = createTestUser();
        TestUser actualRecord = om.readValue(mockMvc
             			.perform(post(BASE_REST_URI).contentType("application/json")
                        .content(om.writeValueAsString(sampleTestUser)))
             			.andDo(print())
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
                .getContentAsString(), TestUser.class);

        TestUser expectedRecord = om
             			.readValue(mockMvc.perform(get(BASE_REST_URI_W_ID + actualRecord.getId()).contentType("application/json"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
                        .getContentAsString(), TestUser.class);

		assertTrack(actualRecord, expectedRecord);
	}

	@Test
    public void testGetByID_NotFound() throws Exception {
		long nonExistentId = 999L;
		mockMvc.perform(get(BASE_REST_URI_W_ID + nonExistentId).contentType("application/json"))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

    private void assertTrack(TestUser actualRecord, TestUser expectedRecord) {
		Assertions.assertTrue(new ReflectionEquals(actualRecord).matches(expectedRecord));
	}

}
