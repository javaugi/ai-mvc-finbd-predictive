/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.repository.TestUserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@lombok.extern.slf4j.Slf4j
@Service
//@RequiredArgsConstructor
public class TestUserService {

    private final TestUserRepository testUserRepository;

    public TestUserService(TestUserRepository testUserRepository) {
        this.testUserRepository = testUserRepository;
    }

    public long getCount() {
        return testUserRepository.count();
    }

    public List<TestUser> getAll() {
        return testUserRepository.findAll();
    }

    public Optional<TestUser> getById(Long id) {
        return this.testUserRepository.findById(id);
    }

    public List<TestUser> getByFirstName(String firstName) {
        return this.testUserRepository.findByFirstName(firstName);
    }

    public TestUser save(TestUser testUser) {
        return testUserRepository.save(testUser);
    }

    public TestUser update(TestUser testUser) {
        return testUserRepository.save(testUser);
    }
    
    public void deleteById(Long id) {
        testUserRepository.deleteById(id);
    }
}
