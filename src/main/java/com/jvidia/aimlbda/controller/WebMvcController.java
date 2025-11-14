/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.controller;

import com.jvidia.aimlbda.entity.AuditLog;
import com.jvidia.aimlbda.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebMvcController {
    private final AuditLogRepository auditLogRepository;

    @RequestMapping("/{page:^(?!.*[.].*$).*$}")
    public String forwardTo(@PathVariable("page") String page) {
        String htmlPage = "/" + page + ".html";
        log.debug("forwarding request to {}", htmlPage);
        return htmlPage;
    }

    // to display all auditLogs at localhost:8080
    // to see database values at localhost:8080/h2-console    
    @GetMapping
    public ResponseEntity<Collection<AuditLog>> getAllAuditlogs(HttpServletRequest request) {
        List<AuditLog> auditLogs = auditLogRepository.findAll();
        String category = request.getParameter("category");
        if (category == null || category.isEmpty()) {
            return ResponseEntity.ok(auditLogs);
        }
        
        auditLogs = auditLogs.stream().filter(p -> category.contains(p.getCategory()))
                .collect(Collectors.toList());
                        
        return ResponseEntity.ok(auditLogs);
    }
        
    @GetMapping("/catagory")
    public ResponseEntity<Collection<AuditLog>> getAllAuditlogsByBindParam(@RequestParam("name") String category) {
        List<AuditLog> auditLogs = auditLogRepository.findAll();
        if (category == null || category.isEmpty()) {
            return ResponseEntity.ok(auditLogs);
        }
        
        auditLogs = auditLogs.stream().filter(p -> category.contains(p.getCategory()))
                .collect(Collectors.toList());
                        
        return ResponseEntity.ok(auditLogs);
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        Optional<AuditLog> opt = auditLogRepository.findById(id);

        //*
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
        // */
        //return ResponseEntity.ok(productOptional.orElse(null));
    }
    
    @GetMapping("/al")
    public ResponseEntity<Collection<AuditLog>> listAuditLogs(HttpServletRequest request, ModelMap modelMap) {
        return ResponseEntity.ok(auditLogRepository.findAll());
    } 
    
    @GetMapping("/al2")
    public ResponseEntity<Collection<AuditLog>> listAuditLogs(RequestEntity<AuditLog> request) {
        return ResponseEntity.ok(auditLogRepository.findAll());
    } 

    @PostMapping
    public ResponseEntity<AuditLog> addAuditLog(RequestEntity<AuditLog> request) {
        AuditLog product = auditLogRepository.save(request.getBody());
        return ResponseEntity.ok(product);
    }

    @PutMapping
    public ResponseEntity<AuditLog> updateAuditLog(RequestEntity<AuditLog> request) {
        AuditLog auditLog = auditLogRepository.save(request.getBody());
        return ResponseEntity.ok(auditLog);
    } 
    
    
    @RequestMapping(value = "/heavy/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> partialUpdateGeneric(@RequestBody AuditLog auditLogUpdates, @PathVariable("id") Long id) {
        Optional<AuditLog> opt = auditLogRepository.findById(id);

        //*
        if (opt.isPresent()) {
            AuditLog auditLog = opt.get();
            BeanUtils.copyProperties(auditLogUpdates, auditLog, new String[]{"id"});
            auditLog = auditLogRepository.save(auditLog);
            return ResponseEntity.ok(auditLog);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
        // */
        //return ResponseEntity.ok(productOptional.orElse(null));
    }    
    
    @DeleteMapping("/{id}") // Map DELETE requests to /products/{id}
    public ResponseEntity<Void> deleteAuditLogById(@PathVariable Long id) {
        Optional<AuditLog> opt = auditLogRepository.findById(id);

        if (opt.isPresent()) {
            auditLogRepository.deleteById(id); // Use deleteById for deleting by ID
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
    }    
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAuditLog(RequestEntity<AuditLog> request) {  
        Optional<AuditLog> opt = Optional.empty();
        long id = 0;
        if (request.getBody() != null && request.getBody().getId() != null) {
            id = request.getBody().getId();            
        }        
        if (id > 0) {
            opt = auditLogRepository.findById(id);
        }
        
        if (id > 0 && opt.isPresent()) {
            auditLogRepository.deleteById(id); // Use deleteById for deleting by ID
            ResponseEntity.noContent().build();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            ResponseEntity.notFound().build();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the product doesn't exist
        }
    }
}


/*
/*
Key Features

RESTful API with HATEOAS:
    Resources include links to related resources
    Follows REST principles
    Self-descriptive messages

Web Interface with Spring MVC:
    Traditional server-side rendering
    Thymeleaf templates for HTML generation
    Simple CRUD operations through web forms

Data Model:
    JPA entities with proper relationships
    Repository pattern for data access

Separation of Concerns:
    API endpoints separate from web interface
    Clear distinction between data model and resource representation

This implementation provides a solid foundation that can be extended with additional features like:

    Authentication and authorization
    Validation
    Advanced search capabilities
    Pagination
    Caching
    API documentation with Swagger
*/
