package com.evofun.userservice.feature.authentication.exception;

import com.evofun.userservice.shared.exception.AppException;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException(String developerMessage) {
        super(developerMessage, "Invalid credentials");
    }
}