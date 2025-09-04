package com.evofun.userservice.rest.exception;

import com.evofun.userservice.common.error.FieldErrorDto;
import lombok.Getter;
import java.util.List;

@Getter
public class AlreadyExistsException extends RuntimeException {
    /// mb extend from my AppException...
    private final List<FieldErrorDto> errors;
    public AlreadyExistsException(List<FieldErrorDto> errors) {
        this.errors = errors;
    }
}