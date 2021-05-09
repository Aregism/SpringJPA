package com.test.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AdminExistsException extends Exception {
    public AdminExistsException() {
    }

    public AdminExistsException(String message) {
        super(message);
    }
}
