/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.dto.TestUserDTO;
import com.jvidia.aimlbda.dto.TestUserProjection;
import com.jvidia.aimlbda.entity.TestUser;
import com.jvidia.aimlbda.repository.TestUserRepository;
import com.jvidia.aimlbda.service.TestUserService;
import com.jvidia.aimlbda.service.procedure.TestUserServiceEntityManager;
import com.jvidia.aimlbda.service.procedure.TestUserServiceJdbc;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/testonly")
public class TestUserController {

    final TestUserService testUserService;
    final TestUserServiceJdbc userServiceJdbc;
    final TestUserRepository testUserRepository;
    final TestUserServiceEntityManager testUServiceEntityManager;

    public TestUserController(TestUserService testUserService, TestUserServiceJdbc userServiceJdbc,
            TestUserRepository testUserRepository, TestUserServiceEntityManager testUserServiceEntityManager) {
        this.testUserService = testUserService;
        this.userServiceJdbc = userServiceJdbc;
        this.testUserRepository = testUserRepository;
        this.testUServiceEntityManager = testUserServiceEntityManager;

    }

    @GetMapping
    public ResponseEntity<Collection<TestUser>> getAll() {
        return ResponseEntity.ok(testUserService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestUser> getById(@PathVariable Long id) {
        Optional<TestUser> opt = testUserService.getById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{firstName}")
    public ResponseEntity<Collection<TestUser>> getUsersByFirstName(@PathVariable String firstName) {
        return ResponseEntity.ok(this.testUserService.getByFirstName(firstName));
    }

    @PostMapping
    public ResponseEntity<TestUser> create(@RequestBody TestUser testUser) {
        testUser = testUserService.save(testUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(testUser);
        //return ResponseEntity.created(URI.create("/api/testonly/" + testUser.getId())).body(testUser);
    }

    @PutMapping
    public ResponseEntity<TestUser> update(@RequestBody TestUser testUser) {
        testUser = testUserService.save(testUser);
        ResponseEntity.status(HttpStatus.CREATED).body(testUser);
        return ResponseEntity.created(URI.create("/api/testonly/" + testUser.getId())).body(testUser);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<TestUser> opt = testUserService.getById(id);

        if (opt.isPresent()) {
            testUserService.deleteById(id); // Use deleteById for deleting by ID
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // the following are procedure related code
    @GetMapping("/user/jdbc/{city}")
    public List<TestUserDTO> getUsersByCityJdbc(@PathVariable String city,
            @RequestParam(defaultValue = "0") Integer minAge) {
        return userServiceJdbc.findUsersByCity(city, minAge);
    }

    @GetMapping("/user/entitymanager/{city}")
    public List<TestUserDTO> getUsersByCityEntityManager(@PathVariable String city,
            @RequestParam(defaultValue = "0") Integer minAge) {
        return testUServiceEntityManager.findUsersByCityAsDTO(city, minAge);
    }

    @GetMapping("/user/procedure1/{city}")
    public List<TestUserProjection> findUsersByCity(@PathVariable String city,
            @RequestParam(defaultValue = "0") Integer minAge) {
        return testUserRepository.findUsersByCity(city, minAge);
    }

    @GetMapping("/user/procedure2/{city}")
    public List<Object[]> findUsersByCityDirect(@PathVariable String city,
            @RequestParam(defaultValue = "0") Integer minAge) {
        return testUserRepository.findUsersByCityDirect(city, minAge);
    }

    @GetMapping("/user/procedure3/{userId}")
    public Object[] updateUserAge(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer minAge) {
        return testUserRepository.updateUserAge(userId, minAge);
    }
}
