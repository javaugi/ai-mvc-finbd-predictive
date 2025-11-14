/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils.mapper;

import com.jvidia.aimlbda.dto.UsersDTO;
import com.jvidia.aimlbda.entity.Users;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface UsersMapperCustom {

    UsersDTO toDto(Users user);

	default Integer calculateAge(LocalDate birthDate) {
		if (birthDate == null) {
			return null;
		}
        return Period.between(birthDate, OffsetDateTime.now().toLocalDate()).getYears();
	}

	// After mapping customization
	@AfterMapping
    default void calculateAge(Users user, @MappingTarget UsersDTO userDTO) {
        userDTO.setAge(calculateAge(user.getBirthDate().toLocalDate()));
	}

}
