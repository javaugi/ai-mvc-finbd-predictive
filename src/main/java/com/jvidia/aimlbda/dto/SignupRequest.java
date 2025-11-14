package com.jvidia.aimlbda.dto;


import com.jvidia.aimlbda.entity.UserRole;
import com.jvidia.aimlbda.utils.types.AuthProvider;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    private int id;

    private String username;
    @Email(message = "Must be email")
    @NotBlank(message = "Email must not be empty")
    private String email;
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;

    private String name;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private OffsetDateTime birthDate;
    private Integer age;

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    private String imageUrl;
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    private String providerId;
    private String registrationId;

    private OffsetDateTime createdDate;
    private OffsetDateTime updatedDate;

    private List<UserRole> userRoles;
}
