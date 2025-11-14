package com.jvidia.aimlbda.repository;


import com.jvidia.aimlbda.entity.UserInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByUsername(String username);

    List<UserInfo> findByEmailIn(Set<String> emails);

    @Query("SELECT u FROM UserInfo u WHERE u.email IN :emails")
    List<UserInfo> findUsersByEmails(@Param("emails") List<String> emails);
}
