package com.evofun.userservice.rest.exception;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException(String developerMessage) {
        super(developerMessage, "Invalid credentials");
    }
}