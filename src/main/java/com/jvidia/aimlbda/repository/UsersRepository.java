/*
 * Copyright (C) 2019 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.jvidia.aimlbda.repository;

import com.jvidia.aimlbda.entity.Users;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {

    Optional<Users> findByUsername(@Param("username") String username);
    
    Optional<Users> findByEmail(@Param("email") String email);
}
