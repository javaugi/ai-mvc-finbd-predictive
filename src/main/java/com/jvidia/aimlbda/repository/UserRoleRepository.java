package com.jvidia.aimlbda.repository;

import com.jvidia.aimlbda.entity.Role;
import com.jvidia.aimlbda.entity.UserInfo;
import com.jvidia.aimlbda.entity.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.role = :role and ur.userInfo = :userInfo ")
    Optional<UserRole> findByRoleAndUserInfo(@Param("role") Role role, @Param("userInfo") UserInfo userInfo);
}
