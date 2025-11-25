package com.jvidia.aimlbda.entity;

import jakarta.persistence.*;
import java.util.HashSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserRole> userRoles;

    // --- Defining the Many-to-Many Relationship ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_privileges", // Name of the join table (from SQL definition)
            joinColumns = @JoinColumn(name = "role_id"), // Foreign key column in 'role_privilege' referencing 'role'
            inverseJoinColumns = @JoinColumn(name = "privilege_id") // Foreign key column in 'role_privilege' referencing 'privilege'
    )
    @Builder.Default
    private Set<Privilege> privileges = new HashSet<>();

    // --- Convenience Methods ---
    public void addPrivilege(Privilege privilege) {
        this.privileges.add(privilege);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}