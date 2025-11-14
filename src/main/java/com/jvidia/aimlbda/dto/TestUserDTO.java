/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestUserDTO {
    private Long id;

    private String username;
    private String password;

    private String name;
    private String firstName;
    private String lastName;

    private String email;
    private String phone;
    private String city;

    private Integer age;
    private String roles;

    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;
}
