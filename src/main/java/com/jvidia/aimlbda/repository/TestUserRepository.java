package com.jvidia.aimlbda.repository;

import com.jvidia.aimlbda.dto.TestUserProjection;
import com.jvidia.aimlbda.entity.TestUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestUserRepository extends JpaRepository<TestUser, Long> {

    public Optional<TestUser> findByUsername(String username);

    public List<TestUser> findByFirstName(String firstName);

    @Procedure(name = "TestUser.findByCity")
    public List<TestUserProjection> findUsersByCity(@Param("p_city") String city, @Param("p_min_age") Integer minAge);

    // Alternative approach using direct procedure name
    @Procedure(procedureName = "find_users_by_city")
    public List<Object[]> findUsersByCityDirect(@Param("p_city") String city, @Param("p_min_age") Integer minAge);

    @Procedure(procedureName = "update_user_age")
    public Object[] updateUserAge(@Param("p_user_id") Long userId, @Param("p_new_age") Integer newAge);
}
