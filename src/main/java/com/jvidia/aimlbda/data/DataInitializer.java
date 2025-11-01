/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.data;

import com.jvidia.aimlbda.clients.AuditLogClient;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        //populateData();
    }
    
    @Autowired
    private AuditLogClient auditLogClient;

    @PostConstruct
    public void populateData() {
        auditLogClient.setup();
    }    
    
    /*
    private void populateData() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(DataInitializer.class);

        AuditLogClient client = context.getBean(AuditLogClient.class);
        client.setup();
        EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
        emf.close();
    }    
    // */
}