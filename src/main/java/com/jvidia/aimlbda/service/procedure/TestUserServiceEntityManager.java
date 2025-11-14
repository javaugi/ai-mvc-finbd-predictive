/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.procedure;

import com.jvidia.aimlbda.dto.TestUserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TestUserServiceEntityManager {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Object[]> findUsersByCity(String city, Integer minAge) {
        StoredProcedureQuery query = entityManager
                .createNamedStoredProcedureQuery("TestUser.findByCity")
                .setParameter("p_city", city)
                .setParameter("p_min_age", minAge);

        return query.getResultList();
    }

    // Process the result
    public List<TestUserDTO> findUsersByCityAsDTO(String city, Integer minAge) {
        List<Object[]> results = findUsersByCity(city, minAge);
        return results.stream().map(this::mapToUserDTO).collect(Collectors.toList());
    }

    private TestUserDTO mapToUserDTO(Object[] result) {
        TestUserDTO user = new TestUserDTO();
        user.setId(((Number) result[0]).longValue());
        user.setUsername((String) result[1]);
        user.setEmail((String) result[2]);
        user.setCity((String) result[3]);
        user.setAge(((Number) result[4]).intValue());
        return user;
    }
}
