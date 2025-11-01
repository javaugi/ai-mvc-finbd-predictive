/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import com.jvidia.aimlbda.entity.AuditLog;
import com.jvidia.aimlbda.repository.AuditLogRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditLogClient {    
    
    @Autowired
    AuditLogRepository auditLogRepository;
    
    @PostConstruct
    public void init() {
        log.info("AuditLogClient init");
        try{
            setup();
        }catch(Exception ex) {
            log.error("Error AuditLogClient init ", ex);
        }
    }    

    public void setup() {
        try{
            List<String> categories = Arrays.asList("Sales", "Expenses", "Inventory", "Marketing", "HR");
            List<String> regions = Arrays.asList("North", "South", "East", "West", "Central");
            Random random = new Random();
            
            List<AuditLog> auditLogs = new ArrayList<>();
            AuditLog entity;
            for (int i = 0; i < 200; i++) {
                entity = new AuditLog();
                entity.setCategory(categories.get(random.nextInt(categories.size())));
                entity.setAuditValue(1000 + random.nextDouble() * 9000);
                entity.setAuditDate(Instant.now().minus(random.nextInt(365), ChronoUnit.DAYS));
                entity.setRegion(regions.get(random.nextInt(regions.size())));
                entity.setDescription("Sample data entry #" + (i + 1));
                
                auditLogs.add(entity);
            }
            
            auditLogRepository.saveAll(auditLogs);
            log.info("Total Records added {}", auditLogRepository.count());
        }catch(Exception e) {
            log.error("Error setup {}", e);
        }
        
    }

}

