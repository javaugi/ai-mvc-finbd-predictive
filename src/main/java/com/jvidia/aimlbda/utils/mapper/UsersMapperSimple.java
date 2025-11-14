/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils.mapper;

import com.jvidia.aimlbda.dto.UsersDTO;
import com.jvidia.aimlbda.entity.Users;
import org.springframework.stereotype.Service;

/**
 * @author javau
 */
@Service
public class UsersMapperSimple {

	// Entity to DTO
	public UsersDTO toDto(Users user) {
        UsersDTO dto = new UsersDTO();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		return dto;
	}

	// DTO to Entity
	public Users toEntity(UsersDTO dto) {
        Users user = Users.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
		return user;
	}

}
