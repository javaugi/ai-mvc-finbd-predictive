/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.repository;

import com.jvidia.aimlbda.entity.AuditLog;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByCategory(String category);
    
    @Query("SELECT d FROM AuditLog d WHERE d.auditDate BETWEEN :startDate AND :endDate")
    List<AuditLog> findByDateRange(Instant startDate, Instant endDate);
}
