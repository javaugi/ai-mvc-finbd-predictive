package com.jvidia.aimlbda.security.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateUserException extends DataIntegrityViolationException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
