/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jvidia.aimlbda.security.validator;

import com.jvidia.aimlbda.utils.types.RoleType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author javaugi
 */
public class RoleValidator implements ConstraintValidator<ValidateRole, String> {

	@Override
	public boolean isValid(String role, ConstraintValidatorContext context) {
		if (role == null) {
			return false;
		}

		try {
            RoleType.valueOf("ROLE_" + role.toUpperCase());
			return true;
		}
		catch (IllegalArgumentException e) {
			return false;
		}
	}

}
