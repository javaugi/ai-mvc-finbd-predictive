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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {

	private Long id;
    private String name;
    private String email;
    private String username;
    private String password;

    private String lastName;
    private String firstName;
    private String middleInitial;

    private OffsetDateTime birthDate;
    private Integer age;

    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;

}
