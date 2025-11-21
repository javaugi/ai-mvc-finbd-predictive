/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.clients;

import com.jvidia.aimlbda.entity.AuditLog;
import com.jvidia.aimlbda.repository.AuditLogRepository;
import java.time.OffsetDateTime;
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

    public void setup() {
        try {
            if (auditLogRepository.count() > 50) {
                log.info("AuditLogClient.setup() count {}", auditLogRepository.count());
                return;
            }

            List<String> categories = Arrays.asList("Sales", "Expenses", "Inventory", "Marketing", "HR");
            List<String> regions = Arrays.asList("North", "South", "East", "West", "Central");
            Random random = new Random();
            
            List<AuditLog> auditLogs = new ArrayList<>();
            AuditLog entity;
            for (int i = 0; i < 50; i++) {
                entity = new AuditLog();
                entity.setCategory(categories.get(random.nextInt(categories.size())));
                entity.setAuditValue(1000 + random.nextDouble() * 9000);
                entity.setAuditDate(OffsetDateTime.now().minus(random.nextInt(365), ChronoUnit.DAYS));
                entity.setRegion(regions.get(random.nextInt(regions.size())));
                entity.setDescription("Sample data entry #" + (i + 1));
                
                auditLogs.add(entity);
            }
            
            auditLogRepository.saveAll(auditLogs);
            log.info("AuditLogClient.setup() records added count {}", auditLogRepository.count());
        } catch (Exception e) {
            log.error("Error setup {}", e);
        }
    }

}

