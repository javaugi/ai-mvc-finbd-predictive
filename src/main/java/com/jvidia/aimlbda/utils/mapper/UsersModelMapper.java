/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.utils.mapper;

import com.jvidia.aimlbda.dto.UsersDTO;
import com.jvidia.aimlbda.entity.Users;
import org.modelmapper.ModelMapper;

/**
 * @author javau
 */
public class UsersModelMapper {

	ModelMapper modelMapper = new ModelMapper();

    Users users = Users.builder().build();

    UsersDTO dto = modelMapper.map(users, UsersDTO.class);

    Users entity = modelMapper.map(dto, Users.class);

}
