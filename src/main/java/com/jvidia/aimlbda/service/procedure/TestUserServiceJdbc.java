/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.service.procedure;

import com.jvidia.aimlbda.dto.TestUserDTO;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
public class TestUserServiceJdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @SuppressWarnings("deprecation")   
    public List<TestUserDTO> findUsersByCity(String city, Integer minAge) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("find_users_by_city");
        call.execute(Map.of("p_acc_id", 101, "p_amt", 500));

        String sql = "SELECT * FROM find_users_by_city(?, ?)";

        return jdbcTemplate.query(sql, new Object[]{city, minAge}, (rs, rowNum) -> {
            TestUserDTO user = new TestUserDTO();
            user.setId(rs.getLong("user_id"));
            user.setUsername(rs.getString("user_name"));
            user.setEmail(rs.getString("user_email"));
            user.setCity(rs.getString("user_city"));
            user.setAge(rs.getInt("user_age"));
            return user;
        });
    }
}
