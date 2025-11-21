/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import com.jvidia.aimlbda.data.*;
import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.repository.TestUserRepository;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestUserClient extends DataGeneratorBase {

    private final TestUserRepository testUserRepository;

    public void setup() {
        try {
            if (testUserRepository.count() >= 50) {
                log.info("TestUserClient.setup() count {}", testUserRepository.count());
                return;
            }

            List<TestUser> testUsers = new ArrayList<>();
            TestUser testUser;
            for (int i = 0; i < 50; i++) {
                testUser = generate(getUPWD(i));
                testUsers.add(testUser);
            }

            this.testUserRepository.saveAll(testUsers);
            log.info("TestUserClient.setup() records added count {}", testUserRepository.count());
        } catch (Exception ex) {
            log.error("Error setup ", ex);

        }
    }

    private String getUPWD(int i) {
        if (i < unames.size()) {
            return unames.get(i);
        }

        return FAKER.name().username();
    }

    public static TestUser generate(String username) {
        return generate(username, new BCryptPasswordEncoder());
    }
    
    public static TestUser generate(String username, PasswordEncoder passwordEncoder) {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        int age = FAKER.number().numberBetween(18, 60);
        String stateAbbr = FAKER.address().stateAbbr();
        TestUser testUser = TestUser.builder()
                //.id(UUID.randomUUID().toString())
            .username(username)
                .email(FAKER.internet().emailAddress())
                .password(passwordEncoder.encode(username))
                .name(firstName + " " + lastName)
                .firstName(firstName)
                .lastName(firstName)
                .middleInitial(firstName.substring(0, 1))
                .birthDate(FAKER.date().past(age, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
                .age(age)
                .streetAddress(FAKER.address().streetAddress())
                .city(FAKER.address().city())
                .state(stateAbbr)
                .zipCode(FAKER.address().zipCodeByState(stateAbbr))
                .createdDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))            
            .build();
        
        return testUser;
    }

}
