package com.jvidia.aimlbda.entity;

import com.jvidia.aimlbda.dto.TestUserProjection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
@Table(name = "test_users")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
            name = "TestUser.findByCity",
            procedureName = "find_users_by_city",
            parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_city", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_min_age", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class)
            },
            resultClasses = TestUserProjection.class
    )
})
public class TestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

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
