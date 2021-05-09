package com.test.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CodeExpiredException extends Exception{
    public CodeExpiredException() {
    }

    public CodeExpiredException(String message) {
        super(message);
    }
}
