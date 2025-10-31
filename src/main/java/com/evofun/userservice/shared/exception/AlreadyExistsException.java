package com.evofun.userservice.shared.exception;

import com.evofun.userservice.shared.exception.model.FieldErrorDto;
import lombok.Getter;
import java.util.List;

@Getter
public class AlreadyExistsException extends RuntimeException {
    //TODO extend from my AppException...
    private final List<FieldErrorDto> errors;
    public AlreadyExistsException(List<FieldErrorDto> errors) {
        this.errors = errors;
    }
}