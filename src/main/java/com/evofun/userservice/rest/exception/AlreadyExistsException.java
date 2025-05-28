package com.evofun.userservice.rest.exception;

import com.evofun.userservice.common.error.FieldErrorDto;
import lombok.Getter;

import java.util.List;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final List<FieldErrorDto> errors;
    public AlreadyExistsException(/*String message, */List<FieldErrorDto> errors) {
//        super(message);
        this.errors = errors;
    }
}
