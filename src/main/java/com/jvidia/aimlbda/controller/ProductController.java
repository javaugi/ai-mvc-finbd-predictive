package com.jvidia.aimlbda.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String product(){
        return "This is user Product";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String productAdmin(){
        return "This is admin Product";
    }
}
