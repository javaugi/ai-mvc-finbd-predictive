/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.test;

import com.jvidia.aimlbda.config.DatabaseProperties;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev") // or "dev", "test", "prod"
class DatabaseDevPropertiesTest {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Test
    void testDatabasePropertiesLoaded() {
        assertNotNull(databaseProperties.getUrl());
        assertNotNull(databaseProperties.getUsername());
        assertNotNull(databaseProperties.getDriverClassName());

        System.out.println("Database URL: " + databaseProperties.getUrl());
        System.out.println("Database Username: " + databaseProperties.getUsername());
    }
}
