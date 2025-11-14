package com.jvidia.aimlbda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    private String name;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "middle_initial")
    private String middleInitial;
    @Column(name = "birth_date")
    private OffsetDateTime birthDate;
    private Integer age;

    @Column(name = "street_address")
    private String streetAddress;
    private String city;
    private String state;
    @Column(name = "zip_code")
    private String zipCode;

    @CreatedDate
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private OffsetDateTime updatedDate;
}
